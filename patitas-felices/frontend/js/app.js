const API = new URLSearchParams(location.search).get("api") || "http://localhost:8080";
const CLAVE_TOKEN = "patitas-token";

const estado = {
    token: null,
    usuario: null,
    duenos: [],
    mascotas: [],
    veterinarios: [],
    turnos: [],
    medicamentos: [],
    edicion: { dueno: null, mascota: null, veterinario: null, medicamento: null },
    duenoConMascotasVisibles: null
};

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => document.querySelectorAll(selector);

class ErrorApi extends Error {
    constructor(status, mensaje) {
        super(mensaje);
        this.status = status;
    }
}

function esc(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, (c) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", "\"": "&quot;", "'": "&#39;"
    })[c]);
}

function leerToken() {
    try {
        return sessionStorage.getItem(CLAVE_TOKEN);
    } catch (e) {
        return null;
    }
}

function guardarToken(token) {
    try {
        if (token) {
            sessionStorage.setItem(CLAVE_TOKEN, token);
        } else {
            sessionStorage.removeItem(CLAVE_TOKEN);
        }
    } catch (e) {
    }
}

function decodificarToken(token) {
    try {
        const parte = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
        const relleno = parte + "=".repeat((4 - parte.length % 4) % 4);
        return JSON.parse(atob(relleno));
    } catch (e) {
        return null;
    }
}

function textoEstado(status) {
    const textos = {
        0: "No se pudo conectar con el gateway en " + API,
        400: "Los datos enviados no son válidos",
        401: "No autorizado: falta el token o no es válido",
        403: "Acceso denegado",
        404: "No se encontró el recurso",
        409: "Conflicto con una regla de negocio",
        429: "Demasiadas peticiones: el gateway aplicó el límite de velocidad",
        500: "Error interno del servidor",
        503: "Servicio no disponible"
    };
    return textos[status] || "Error " + status;
}

async function api(metodo, ruta, cuerpo, opciones = {}) {
    const { conToken = true, tokenForzado, silencioso = false } = opciones;
    const headers = {};
    if (cuerpo !== undefined) {
        headers["Content-Type"] = "application/json";
    }
    const token = tokenForzado !== undefined ? tokenForzado : estado.token;
    if (conToken && token) {
        headers.Authorization = "Bearer " + token;
    }

    const inicio = performance.now();
    let respuesta;
    try {
        respuesta = await fetch(API + ruta, {
            method: metodo,
            headers,
            body: cuerpo !== undefined ? JSON.stringify(cuerpo) : undefined
        });
    } catch (e) {
        registrar(metodo, ruta, 0, performance.now() - inicio);
        throw new ErrorApi(0, textoEstado(0));
    }
    registrar(metodo, ruta, respuesta.status, performance.now() - inicio);

    if (respuesta.status === 401 && conToken && tokenForzado === undefined && !silencioso) {
        cerrarSesion("La sesión expiró o el token dejó de ser válido. Volvé a ingresar.");
        throw new ErrorApi(401, textoEstado(401));
    }

    if (!respuesta.ok) {
        let mensaje = null;
        try {
            const datos = await respuesta.json();
            mensaje = datos.mensaje || datos.error;
        } catch (e) {
        }
        throw new ErrorApi(respuesta.status, mensaje || textoEstado(respuesta.status));
    }

    if (respuesta.status === 204) {
        return null;
    }
    const texto = await respuesta.text();
    return texto ? JSON.parse(texto) : null;
}

function registrar(metodo, ruta, status, ms) {
    const lista = $("#lista-registro");
    const item = document.createElement("li");
    const hora = new Date().toLocaleTimeString("es-AR", { hour12: false });
    const clase = "c" + String(status).charAt(0);
    item.innerHTML =
        `<span class="hora">${hora}</span>` +
        `<span class="ruta"><span class="metodo">${esc(metodo)}</span> ${esc(ruta)}</span>` +
        `<span class="codigo ${clase}">${status === 0 ? "ERR" : status}</span>` +
        `<span class="duracion">${Math.round(ms)} ms</span>`;
    lista.prepend(item);
    while (lista.children.length > 60) {
        lista.lastElementChild.remove();
    }
}

function aviso(texto, tipo = "ok", titulo) {
    const caja = document.createElement("div");
    caja.className = "aviso" + (tipo === "error" ? " error" : "");
    caja.innerHTML = (titulo ? `<strong>${esc(titulo)}</strong>` : "") + esc(texto);
    $("#avisos").append(caja);
    setTimeout(() => caja.remove(), tipo === "error" ? 6500 : 4000);
}

function avisoError(error) {
    const titulo = error.status ? "Error " + error.status : "Sin conexión";
    aviso(error.message, "error", titulo);
}

function formatearFecha(fechaHora) {
    const fecha = new Date(fechaHora);
    if (Number.isNaN(fecha.getTime())) {
        return fechaHora;
    }
    return fecha.toLocaleString("es-AR", {
        day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit", hour12: false
    });
}

function formatearDia(fecha) {
    if (!fecha) {
        return "—";
    }
    const [anio, mes, dia] = fecha.split("-");
    return `${dia}/${mes}/${anio}`;
}

function datosFormulario(form) {
    const datos = {};
    new FormData(form).forEach((valor, clave) => {
        datos[clave] = typeof valor === "string" ? valor.trim() : valor;
    });
    return datos;
}

function rellenarFormulario(form, datos) {
    Object.entries(datos).forEach(([clave, valor]) => {
        const campo = form.elements.namedItem(clave);
        if (campo) {
            campo.value = valor ?? "";
        }
    });
}

async function conBoton(boton, accion) {
    boton.disabled = true;
    try {
        await accion();
    } catch (error) {
        avisoError(error);
    } finally {
        boton.disabled = false;
    }
}

function nombreDueno(id) {
    const dueno = estado.duenos.find((d) => d.id === id);
    return dueno ? `${dueno.apellido}, ${dueno.nombre}` : `Dueño #${id}`;
}

function nombreMascota(id) {
    const mascota = estado.mascotas.find((m) => m.id === id);
    return mascota ? mascota.nombre : `Mascota #${id}`;
}

function nombreVeterinario(id) {
    const vet = estado.veterinarios.find((v) => v.id === id);
    return vet ? `${vet.apellido}, ${vet.nombre}` : `Veterinario #${id}`;
}

function tabla(columnas, filas, vacio) {
    if (!filas.length) {
        return `<p class="vacio">${esc(vacio)}</p>`;
    }
    const cabecera = columnas.map((c) => `<th>${esc(c)}</th>`).join("");
    return `<div class="tabla-contenedor"><table><thead><tr>${cabecera}</tr></thead><tbody>${filas.join("")}</tbody></table></div>`;
}

function etiquetaEstado(estadoTurno) {
    const textos = { PENDIENTE: "Pendiente", CONFIRMADO: "Confirmado", CANCELADO: "Cancelado" };
    return `<span class="etiqueta ${esc(estadoTurno)}">${esc(textos[estadoTurno] || estadoTurno)}</span>`;
}

function opciones(lista, texto, primera) {
    const base = primera ? `<option value="">${esc(primera)}</option>` : "";
    return base + lista.map((item) => `<option value="${item.id}">${esc(texto(item))}</option>`).join("");
}

function conservarSeleccion(select, html) {
    const anterior = select.value;
    select.innerHTML = html;
    if ([...select.options].some((o) => o.value === anterior)) {
        select.value = anterior;
    }
}

async function cargar(recurso) {
    const rutas = {
        duenos: "/api/duenos",
        mascotas: "/api/mascotas",
        veterinarios: "/api/veterinarios",
        turnos: "/api/turnos",
        medicamentos: "/api/medicamentos"
    };
    estado[recurso] = await api("GET", rutas[recurso]);
}

async function cargarTodo() {
    const recursos = ["duenos", "mascotas", "veterinarios", "turnos", "medicamentos"];
    const resultados = await Promise.allSettled(recursos.map(cargar));
    const fallidos = resultados
        .map((r, i) => (r.status === "rejected" ? recursos[i] : null))
        .filter(Boolean);
    if (fallidos.length && estado.token) {
        aviso(`No se pudieron cargar: ${fallidos.join(", ")}. Revisá que esos microservicios estén levantados.`, "error", "Carga incompleta");
    }
    renderTodo();
}

function renderTodo() {
    renderResumen();
    renderDuenos();
    renderMascotas();
    renderVeterinarios();
    renderTurnos();
    renderMedicamentos();
    renderSelects();
    renderMiAgenda();
    mostrarNombreVeterinario();
}

function renderSelects() {
    const textoDueno = (d) => `${d.apellido}, ${d.nombre}`;
    const textoMascota = (m) => `${m.nombre} · ${nombreDueno(m.duenoId)}`;
    const textoVet = (v) => `${v.apellido}, ${v.nombre} · ${v.especialidad}`;

    conservarSeleccion($("#form-mascota").elements.duenoId, opciones(estado.duenos, textoDueno, "Elegí un dueño"));
    conservarSeleccion($("#filtro-mascota-dueno"), opciones(estado.duenos, textoDueno, "Todos"));
    conservarSeleccion($("#form-turno").elements.mascotaId, opciones(estado.mascotas, textoMascota, "Elegí una mascota"));
    conservarSeleccion($("#form-turno").elements.veterinarioId, opciones(estado.veterinarios, textoVet, "Elegí un veterinario"));
    conservarSeleccion($("#filtro-turno-veterinario"), opciones(estado.veterinarios, (v) => `${v.apellido}, ${v.nombre}`, "Todos"));
    conservarSeleccion($("#filtro-turno-mascota"), opciones(estado.mascotas, (m) => m.nombre, "Todas"));
    const turnosActivos = [...estado.turnos]
        .filter((t) => t.estado !== "CANCELADO")
        .sort((a, b) => new Date(a.fechaHora) - new Date(b.fechaHora));
    conservarSeleccion($("#receta-turno"), opciones(turnosActivos,
        (t) => `#${t.id} · ${formatearFecha(t.fechaHora)} · ${nombreMascota(t.mascotaId)}`, "Elegí un turno"));
    conservarSeleccion($("#form-receta").elements.medicamentoId, opciones(estado.medicamentos,
        (m) => `${m.nombre} · stock ${m.stock}`, "Elegí un medicamento"));
}

function renderResumen() {
    $("#ind-duenos").textContent = estado.duenos.length;
    $("#ind-mascotas").textContent = estado.mascotas.length;
    $("#ind-veterinarios").textContent = estado.veterinarios.length;
    $("#ind-turnos").textContent = estado.turnos.length;
    $("#ind-medicamentos").textContent = estado.medicamentos.length;
    const bajos = estado.medicamentos.filter((m) => m.stock <= 5).length;
    $("#ind-medicamentos-detalle").textContent = bajos ? `${bajos} con stock bajo` : "ms-medicamento";

    const ahora = new Date();
    const proximos = estado.turnos
        .filter((t) => t.estado !== "CANCELADO" && new Date(t.fechaHora) >= ahora)
        .sort((a, b) => new Date(a.fechaHora) - new Date(b.fechaHora))
        .slice(0, 6)
        .map((t) => `<tr>
            <td class="fecha">${esc(formatearFecha(t.fechaHora))}</td>
            <td>${esc(nombreMascota(t.mascotaId))}</td>
            <td>${esc(nombreVeterinario(t.veterinarioId))}</td>
            <td>${etiquetaEstado(t.estado)}</td>
        </tr>`);
    $("#proximos-turnos").innerHTML = tabla(["Fecha", "Mascota", "Veterinario", "Estado"], proximos, "No hay turnos próximos.");

    const total = estado.turnos.length || 1;
    $("#turnos-por-estado").innerHTML = ["PENDIENTE", "CONFIRMADO", "CANCELADO"].map((e) => {
        const cantidad = estado.turnos.filter((t) => t.estado === e).length;
        return `<div class="barra-fila">
            <span>${etiquetaEstado(e)}</span>
            <div class="barra-pista"><div class="barra-relleno ${e}" style="width:${(cantidad / total) * 100}%"></div></div>
            <span class="barra-valor">${cantidad}</span>
        </div>`;
    }).join("");
}

function renderDuenos() {
    if (!estado.duenos.some((d) => d.id === estado.duenoConMascotasVisibles)) {
        estado.duenoConMascotasVisibles = null;
        $("#panel-mascotas-dueno").hidden = true;
    }
    const filas = estado.duenos.map((d) => {
        const cantidad = estado.mascotas.filter((m) => m.duenoId === d.id).length;
        return `<tr>
            <td class="id">${d.id}</td>
            <td>${esc(d.apellido)}, ${esc(d.nombre)}</td>
            <td>${esc(d.email)}</td>
            <td>${esc(d.telefono)}</td>
            <td>${cantidad}</td>
            <td class="acciones">
                <button type="button" class="boton texto chico" data-accion="ver-mascotas" data-id="${d.id}">Mascotas</button>
                <button type="button" class="boton texto chico" data-accion="editar-dueno" data-id="${d.id}">Editar</button>
                <button type="button" class="boton texto chico peligro" data-accion="eliminar-dueno" data-id="${d.id}">Eliminar</button>
            </td>
        </tr>`;
    });
    $("#tabla-duenos").innerHTML = tabla(["#", "Dueño", "Email", "Teléfono", "Mascotas", ""], filas, "Todavía no hay dueños registrados.");
}

function filasMascotas(lista) {
    return lista.map((m) => `<tr>
        <td class="id">${m.id}</td>
        <td>${esc(m.nombre)}</td>
        <td>${esc(m.especie)}</td>
        <td>${esc(m.raza)}</td>
        <td>${esc(formatearDia(m.fechaNacimiento))}</td>
        <td>${esc(nombreDueno(m.duenoId))}</td>
        <td class="acciones">
            <button type="button" class="boton texto chico" data-accion="editar-mascota" data-id="${m.id}">Editar</button>
            <button type="button" class="boton texto chico peligro" data-accion="eliminar-mascota" data-id="${m.id}">Eliminar</button>
        </td>
    </tr>`);
}

const COLUMNAS_MASCOTAS = ["#", "Nombre", "Especie", "Raza", "Nacimiento", "Dueño", ""];

async function renderMascotas() {
    const duenoId = $("#filtro-mascota-dueno").value;
    let lista = estado.mascotas;
    if (duenoId) {
        try {
            lista = await api("GET", `/api/mascotas/dueno/${duenoId}`);
        } catch (error) {
            avisoError(error);
        }
    }
    $("#tabla-mascotas").innerHTML = tabla(COLUMNAS_MASCOTAS, filasMascotas(lista), "No hay mascotas para mostrar.");
}

function renderVeterinarios(lista = estado.veterinarios) {
    const filas = lista.map((v) => `<tr>
        <td class="id">${v.id}</td>
        <td>${esc(v.apellido)}, ${esc(v.nombre)}</td>
        <td>${esc(v.matricula)}</td>
        <td>${esc(v.especialidad)}</td>
        <td>${esc(v.email)}</td>
        <td class="acciones">
            <button type="button" class="boton texto chico" data-accion="editar-veterinario" data-id="${v.id}">Editar</button>
            <button type="button" class="boton texto chico peligro" data-accion="eliminar-veterinario" data-id="${v.id}">Eliminar</button>
        </td>
    </tr>`);
    $("#tabla-veterinarios").innerHTML = tabla(["#", "Veterinario", "Matrícula", "Especialidad", "Email", ""], filas, "No hay veterinarios para mostrar.");
}

async function renderTurnos() {
    const vetId = $("#filtro-turno-veterinario").value;
    const mascotaId = $("#filtro-turno-mascota").value;
    let lista = estado.turnos;
    try {
        if (vetId) {
            lista = await api("GET", `/api/turnos/veterinario/${vetId}`);
        } else if (mascotaId) {
            lista = await api("GET", `/api/turnos/mascota/${mascotaId}`);
        }
    } catch (error) {
        avisoError(error);
    }
    if (vetId && mascotaId) {
        lista = lista.filter((t) => String(t.mascotaId) === mascotaId);
    }

    $("#tabla-turnos").innerHTML = tabla(COLUMNAS_TURNOS, filasTurnos(lista), "No hay turnos para mostrar.");
}

const COLUMNAS_TURNOS = ["#", "Fecha", "Mascota", "Veterinario", "Motivo", "Estado", ""];

function filasTurnos(lista, orden = 1) {
    return [...lista]
        .sort((a, b) => orden * (new Date(a.fechaHora) - new Date(b.fechaHora)))
        .map((t) => {
            const acciones = [];
            if (t.estado === "PENDIENTE") {
                acciones.push(`<button type="button" class="boton texto chico" data-accion="confirmar-turno" data-id="${t.id}">Confirmar</button>`);
            }
            if (t.estado !== "CANCELADO") {
                acciones.push(`<button type="button" class="boton texto chico" data-accion="recetar-turno" data-id="${t.id}">Recetar</button>`);
                acciones.push(`<button type="button" class="boton texto chico peligro" data-accion="cancelar-turno" data-id="${t.id}">Cancelar</button>`);
            }
            return `<tr>
                <td class="id">${t.id}</td>
                <td class="fecha">${esc(formatearFecha(t.fechaHora))}</td>
                <td>${esc(nombreMascota(t.mascotaId))}</td>
                <td>${esc(nombreVeterinario(t.veterinarioId))}</td>
                <td>${esc(t.motivo)}</td>
                <td>${etiquetaEstado(t.estado)}</td>
                <td class="acciones">${acciones.join("")}</td>
            </tr>`;
        });
}

async function renderMiAgenda() {
    const vetId = estado.usuario && estado.usuario.veterinarioId;
    if (!vetId) {
        return;
    }
    const vet = estado.veterinarios.find((v) => v.id === vetId);
    $("#mi-agenda-bajada").innerHTML = vet
        ? `Turnos asignados a <strong>${esc(vet.nombre)} ${esc(vet.apellido)}</strong> (${esc(vet.especialidad)}, matrícula ${esc(vet.matricula)}). El <code>veterinarioId</code> viaja dentro del token JWT emitido por <code>ms-auth</code>.`
        : `La cuenta está vinculada al veterinario #${vetId}, pero su ficha ya no existe en <code>ms-veterinario</code>.`;
    try {
        const lista = await api("GET", `/api/turnos/veterinario/${vetId}`);
        const ahora = new Date();
        const activo = (t) => t.estado !== "CANCELADO" && new Date(t.fechaHora) >= ahora;
        $("#mi-agenda-proximos").innerHTML = tabla(COLUMNAS_TURNOS, filasTurnos(lista.filter(activo)), "No tenés turnos próximos.");
        $("#mi-agenda-historial").innerHTML = tabla(COLUMNAS_TURNOS, filasTurnos(lista.filter((t) => !activo(t)), -1), "Todavía no hay turnos pasados o cancelados.");
    } catch (error) {
        avisoError(error);
    }
}

const PRECIO = new Intl.NumberFormat("es-AR", { style: "currency", currency: "ARS" });

function renderMedicamentos() {
    const filas = estado.medicamentos.map((m) => {
        const clase = m.stock === 0 ? "agotado" : m.stock <= 5 ? "bajo" : "";
        return `<tr>
            <td class="id">${m.id}</td>
            <td>${esc(m.nombre)}</td>
            <td>${esc(m.principioActivo)}</td>
            <td><span class="stock ${clase}">${m.stock}</span></td>
            <td>${esc(PRECIO.format(m.precioUnitario))}</td>
            <td class="acciones">
                <button type="button" class="boton texto chico" data-accion="editar-medicamento" data-id="${m.id}">Editar</button>
                <button type="button" class="boton texto chico peligro" data-accion="eliminar-medicamento" data-id="${m.id}">Eliminar</button>
            </td>
        </tr>`;
    });
    $("#tabla-medicamentos").innerHTML = tabla(["#", "Nombre", "Principio activo", "Stock", "Precio", ""], filas, "Todavía no hay medicamentos cargados.");
}

async function renderRecetasTurno() {
    const turnoId = $("#receta-turno").value;
    if (!turnoId) {
        $("#recetas-turno").innerHTML = "";
        return;
    }
    try {
        const recetas = await api("GET", `/api/medicamentos/recetas/turno/${turnoId}`);
        const filas = recetas.map((r) => `<tr>
            <td>${esc(r.medicamentoNombre)}</td>
            <td class="fecha">${esc(formatearFecha(r.fecha))}</td>
        </tr>`);
        $("#recetas-turno").innerHTML = `<h4>Recetado en el turno #${esc(turnoId)}</h4>` +
            tabla(["Medicamento", "Recetado el"], filas, "Todavía no se recetó nada en este turno.");
    } catch (error) {
        avisoError(error);
    }
}

async function guardarMedicamento(evento) {
    evento.preventDefault();
    const form = evento.target;
    await conBoton(form.querySelector("[type=submit]"), async () => {
        const datos = datosFormulario(form);
        datos.stock = Number(datos.stock);
        datos.precioUnitario = Number(datos.precioUnitario);
        const id = estado.edicion.medicamento;
        if (id) {
            await api("PUT", `/api/medicamentos/${id}`, datos);
            aviso("Medicamento actualizado.");
        } else {
            await api("POST", "/api/medicamentos", datos);
            aviso("Medicamento registrado.");
        }
        terminarEdicion("medicamento", TITULOS.medicamento);
        await cargar("medicamentos");
        renderTodo();
    });
}

async function recetar(evento) {
    evento.preventDefault();
    const form = evento.target;
    await conBoton(form.querySelector("[type=submit]"), async () => {
        const datos = datosFormulario(form);
        const receta = await api("POST", `/api/medicamentos/${datos.medicamentoId}/recetas`, { turnoId: Number(datos.turnoId) });
        aviso(`Se recetó ${receta.medicamentoNombre} en el turno #${receta.turnoId}. Stock descontado.`);
        await cargar("medicamentos");
        renderTodo();
        await renderRecetasTurno();
    });
}

function irARecetar(turnoId) {
    mostrarSeccion("medicamentos");
    $("#receta-turno").value = String(turnoId);
    renderRecetasTurno();
    $("#form-receta").scrollIntoView({ behavior: "smooth", block: "center" });
}

function mostrarSeccion(nombre) {
    $$(".seccion").forEach((s) => {
        s.hidden = s.dataset.seccion !== nombre;
    });
    $$(".menu-item").forEach((b) => b.classList.toggle("activo", b.dataset.seccion === nombre));
}

function iniciarEdicion(tipo, datos, titulo) {
    estado.edicion[tipo] = datos.id;
    const form = $(`#form-${tipo}`);
    rellenarFormulario(form, datos);
    $(`#titulo-form-${tipo}`).textContent = titulo;
    form.querySelector(`[data-cancelar="${tipo}"]`).hidden = false;
    form.scrollIntoView({ behavior: "smooth", block: "center" });
}

function terminarEdicion(tipo, tituloNuevo) {
    estado.edicion[tipo] = null;
    const form = $(`#form-${tipo}`);
    form.reset();
    $(`#titulo-form-${tipo}`).textContent = tituloNuevo;
    form.querySelector(`[data-cancelar="${tipo}"]`).hidden = true;
}

const TITULOS = { dueno: "Nuevo dueño", mascota: "Nueva mascota", veterinario: "Nuevo veterinario", medicamento: "Nuevo medicamento" };

async function guardarDueno(evento) {
    evento.preventDefault();
    const form = evento.target;
    await conBoton(form.querySelector("[type=submit]"), async () => {
        const datos = datosFormulario(form);
        const id = estado.edicion.dueno;
        if (id) {
            await api("PUT", `/api/duenos/${id}`, datos);
            aviso("Dueño actualizado.");
        } else {
            await api("POST", "/api/duenos", datos);
            aviso("Dueño registrado.");
        }
        terminarEdicion("dueno", TITULOS.dueno);
        await cargar("duenos");
        renderTodo();
    });
}

async function guardarMascota(evento) {
    evento.preventDefault();
    const form = evento.target;
    await conBoton(form.querySelector("[type=submit]"), async () => {
        const datos = datosFormulario(form);
        datos.duenoId = Number(datos.duenoId);
        datos.fechaNacimiento = datos.fechaNacimiento || null;
        const id = estado.edicion.mascota;
        if (id) {
            await api("PUT", `/api/mascotas/${id}`, datos);
            aviso("Mascota actualizada.");
        } else {
            await api("POST", "/api/mascotas", datos);
            aviso("Mascota registrada.");
        }
        terminarEdicion("mascota", TITULOS.mascota);
        await cargar("mascotas");
        renderTodo();
    });
}

async function guardarVeterinario(evento) {
    evento.preventDefault();
    const form = evento.target;
    await conBoton(form.querySelector("[type=submit]"), async () => {
        const datos = datosFormulario(form);
        const id = estado.edicion.veterinario;
        if (id) {
            await api("PUT", `/api/veterinarios/${id}`, datos);
            aviso("Veterinario actualizado.");
        } else {
            await api("POST", "/api/veterinarios", datos);
            aviso("Veterinario registrado.");
        }
        terminarEdicion("veterinario", TITULOS.veterinario);
        await cargar("veterinarios");
        renderTodo();
    });
}

async function guardarTurno(evento) {
    evento.preventDefault();
    const form = evento.target;
    await conBoton(form.querySelector("[type=submit]"), async () => {
        const datos = datosFormulario(form);
        await api("POST", "/api/turnos", {
            fechaHora: datos.fechaHora.length === 16 ? datos.fechaHora + ":00" : datos.fechaHora,
            motivo: datos.motivo,
            mascotaId: Number(datos.mascotaId),
            veterinarioId: Number(datos.veterinarioId)
        });
        aviso("Turno creado en estado pendiente.");
        form.reset();
        await cargar("turnos");
        renderTodo();
    });
}

async function eliminar(ruta, recurso, texto) {
    if (!window.confirm(`¿Eliminar ${texto}? Esta acción no se puede deshacer.`)) {
        return;
    }
    try {
        await api("DELETE", ruta);
        aviso(`Se eliminó ${texto}.`);
        await cargar(recurso);
        renderTodo();
    } catch (error) {
        avisoError(error);
    }
}

async function cambiarEstadoTurno(id, accion) {
    if (accion === "cancelar" && !window.confirm(`¿Cancelar el turno #${id}?`)) {
        return;
    }
    try {
        await api("PATCH", `/api/turnos/${id}/${accion}`);
        aviso(accion === "confirmar" ? `Turno #${id} confirmado.` : `Turno #${id} cancelado.`);
        await cargar("turnos");
        renderTodo();
    } catch (error) {
        avisoError(error);
    }
}

async function verMascotasDeDueno(id) {
    try {
        const lista = await api("GET", `/api/mascotas/dueno/${id}`);
        $("#titulo-mascotas-dueno").textContent = `Mascotas de ${nombreDueno(id)}`;
        $("#tabla-mascotas-dueno").innerHTML = tabla(COLUMNAS_MASCOTAS, filasMascotas(lista), "Este dueño no tiene mascotas registradas.");
        $("#panel-mascotas-dueno").hidden = false;
        estado.duenoConMascotasVisibles = id;
        $("#panel-mascotas-dueno").scrollIntoView({ behavior: "smooth", block: "start" });
    } catch (error) {
        avisoError(error);
    }
}

async function manejarAccion(boton) {
    const id = Number(boton.dataset.id);
    switch (boton.dataset.accion) {
        case "ver-mascotas":
            return verMascotasDeDueno(id);
        case "editar-dueno":
            return iniciarEdicion("dueno", estado.duenos.find((d) => d.id === id), `Editar dueño #${id}`);
        case "eliminar-dueno":
            return eliminar(`/api/duenos/${id}`, "duenos", `al dueño ${nombreDueno(id)}`);
        case "editar-mascota":
            mostrarSeccion("mascotas");
            return iniciarEdicion("mascota", estado.mascotas.find((m) => m.id === id), `Editar mascota #${id}`);
        case "eliminar-mascota":
            return eliminar(`/api/mascotas/${id}`, "mascotas", `a la mascota ${nombreMascota(id)}`);
        case "editar-veterinario":
            return iniciarEdicion("veterinario", estado.veterinarios.find((v) => v.id === id), `Editar veterinario #${id}`);
        case "eliminar-veterinario":
            return eliminar(`/api/veterinarios/${id}`, "veterinarios", `al veterinario ${nombreVeterinario(id)}`);
        case "editar-medicamento":
            return iniciarEdicion("medicamento", estado.medicamentos.find((m) => m.id === id), `Editar medicamento #${id}`);
        case "eliminar-medicamento":
            return eliminar(`/api/medicamentos/${id}`, "medicamentos", `el medicamento ${estado.medicamentos.find((m) => m.id === id)?.nombre ?? "#" + id}`);
        case "recetar-turno":
            return irARecetar(id);
        case "confirmar-turno":
            return cambiarEstadoTurno(id, "confirmar");
        case "cancelar-turno":
            return cambiarEstadoTurno(id, "cancelar");
    }
}

function resultado(salida, correcto, texto) {
    salida.innerHTML = `<span class="resultado ${correcto ? "ok" : "mal"}">${texto}</span>`;
}

async function experimentoSinToken() {
    const salida = $("#res-sin-token");
    try {
        await api("GET", "/api/duenos", undefined, { conToken: false });
        resultado(salida, false, "Respondió 200: el gateway dejó pasar la petición sin token.");
    } catch (error) {
        resultado(salida, error.status === 401, `Respuesta <strong>${error.status}</strong>: ${esc(error.message)}.`);
    }
}

async function experimentoTokenAlterado() {
    const salida = $("#res-token-alterado");
    const token = estado.token;
    const ultimo = token.slice(-1);
    const alterado = token.slice(0, -2) + (token.slice(-2, -1) === "A" ? "B" : "A") + ultimo;
    try {
        await api("GET", "/api/duenos", undefined, { tokenForzado: alterado });
        resultado(salida, false, "Respondió 200: el token alterado fue aceptado.");
    } catch (error) {
        resultado(salida, error.status === 401, `Respuesta <strong>${error.status}</strong>: la firma no coincide con la clave del gateway.`);
    }
}

async function experimentoMascotaInexistente() {
    const salida = $("#res-mascota-inexistente");
    const vet = estado.veterinarios[0];
    if (!vet) {
        resultado(salida, false, "Primero registrá al menos un veterinario.");
        return;
    }
    const fecha = new Date(Date.now() + 400 * 24 * 3600 * 1000);
    fecha.setMinutes(0, 0, 0);
    const iso = new Date(fecha.getTime() - fecha.getTimezoneOffset() * 60000).toISOString().slice(0, 19);
    try {
        await api("POST", "/api/turnos", { fechaHora: iso, motivo: "Experimento", mascotaId: 999999, veterinarioId: vet.id });
        resultado(salida, false, "Se creó el turno: la validación remota no funcionó.");
        await cargar("turnos");
        renderTodo();
    } catch (error) {
        resultado(salida, error.status === 404, `Respuesta <strong>${error.status}</strong>: ${esc(error.message)}.`);
    }
}

async function experimentoRafaga() {
    const salida = $("#res-rafaga");
    salida.innerHTML = `<span class="resultado">Enviando 30 peticiones en paralelo…</span>`;
    const pedidos = Array.from({ length: 30 }, () =>
        api("GET", "/api/turnos", undefined, { silencioso: true }).then(() => 200, (e) => e.status));
    const codigos = await Promise.all(pedidos);
    const conteo = codigos.reduce((acc, c) => ({ ...acc, [c]: (acc[c] || 0) + 1 }), {});
    const detalle = Object.entries(conteo).map(([c, n]) => `${n} × ${c}`).join(" · ");
    const limitadas = conteo[429] || 0;
    resultado(salida, limitadas > 0, limitadas > 0
        ? `${detalle}. El gateway rechazó ${limitadas} con 429 al vaciarse el balde de tokens.`
        : `${detalle}. Ninguna fue limitada: revisá que Redis esté levantado.`);
}

const EXPERIMENTOS = {
    "sin-token": experimentoSinToken,
    "token-alterado": experimentoTokenAlterado,
    "mascota-inexistente": experimentoMascotaInexistente,
    "rafaga": experimentoRafaga
};

const ROLES = { ADMIN: "Administrador", VET: "Veterinario", RECEPCIONISTA: "Recepcionista" };

function mostrarNombreVeterinario() {
    const datos = estado.usuario || {};
    const vet = estado.veterinarios.find((v) => v.id === datos.veterinarioId);
    if (vet) {
        $("#sesion-usuario").textContent = `${datos.sub} · ${vet.nombre} ${vet.apellido} (${vet.matricula})`;
    }
}

function mostrarSesion() {
    const datos = decodificarToken(estado.token) || {};
    estado.usuario = datos;
    $("#menu-mi-agenda").hidden = !datos.veterinarioId;
    $("#sesion-usuario").textContent = `${datos.sub || "usuario"} · ${ROLES[datos.rol] || datos.rol || "sin rol"}`;
    $("#sesion-expira").textContent = datos.exp
        ? "Token válido hasta " + new Date(datos.exp * 1000).toLocaleString("es-AR", { dateStyle: "short", timeStyle: "short" })
        : "";
}

async function entrar(token) {
    estado.token = token;
    guardarToken(token);
    $("#vista-login").hidden = true;
    $("#vista-app").hidden = false;
    mostrarSesion();
    mostrarSeccion(estado.usuario.veterinarioId ? "mi-agenda" : "resumen");
    await cargarTodo();
}

function cerrarSesion(mensaje) {
    estado.token = null;
    estado.usuario = null;
    guardarToken(null);
    ["duenos", "mascotas", "veterinarios", "turnos", "medicamentos"].forEach((r) => {
        estado[r] = [];
    });
    $("#menu-mi-agenda").hidden = true;
    $("#vista-app").hidden = true;
    $("#vista-login").hidden = false;
    if (mensaje) {
        aviso(mensaje, "error", "Sesión cerrada");
    }
}

async function autenticar(evento, ruta) {
    evento.preventDefault();
    const form = evento.target;
    await conBoton(form.querySelector("[type=submit]"), async () => {
        const respuesta = await api("POST", ruta, datosFormulario(form), { conToken: false });
        form.reset();
        alternarMatricula();
        await entrar(respuesta.token);
        aviso(ruta.endsWith("register") ? "Cuenta creada. Ya estás dentro del sistema." : "Sesión iniciada.");
    });
}

function alternarMatricula() {
    const esVeterinario = $("#registro-rol").value === "VET";
    $("#campo-matricula").hidden = !esVeterinario;
    $("#campo-matricula input").disabled = !esVeterinario;
}

function iniciar() {
    $("#registro-rol").addEventListener("change", alternarMatricula);
    $$(".pestana").forEach((pestana) => pestana.addEventListener("click", () => {
        $$(".pestana").forEach((p) => p.classList.toggle("activa", p === pestana));
        $("#form-login").hidden = pestana.dataset.pestana !== "ingresar";
        $("#form-registro").hidden = pestana.dataset.pestana !== "registrarse";
    }));

    $("#form-login").addEventListener("submit", (e) => autenticar(e, "/auth/login"));
    $("#form-registro").addEventListener("submit", (e) => autenticar(e, "/auth/register"));
    $("#boton-salir").addEventListener("click", () => cerrarSesion());

    $$(".menu-item").forEach((b) => b.addEventListener("click", () => mostrarSeccion(b.dataset.seccion)));

    $("#form-dueno").addEventListener("submit", guardarDueno);
    $("#form-mascota").addEventListener("submit", guardarMascota);
    $("#form-veterinario").addEventListener("submit", guardarVeterinario);
    $("#form-turno").addEventListener("submit", guardarTurno);
    $("#form-medicamento").addEventListener("submit", guardarMedicamento);
    $("#form-receta").addEventListener("submit", recetar);
    $("#receta-turno").addEventListener("change", renderRecetasTurno);

    $$("[data-cancelar]").forEach((b) => b.addEventListener("click", () =>
        terminarEdicion(b.dataset.cancelar, TITULOS[b.dataset.cancelar])));

    $("#filtro-mascota-dueno").addEventListener("change", renderMascotas);
    $("#filtro-turno-veterinario").addEventListener("change", renderTurnos);
    $("#filtro-turno-mascota").addEventListener("change", renderTurnos);

    $("#filtro-especialidad").addEventListener("submit", async (e) => {
        e.preventDefault();
        const especialidad = datosFormulario(e.target).especialidad;
        if (!especialidad) {
            renderVeterinarios();
            return;
        }
        try {
            renderVeterinarios(await api("GET", `/api/veterinarios/especialidad/${encodeURIComponent(especialidad)}`));
        } catch (error) {
            avisoError(error);
        }
    });
    $("#limpiar-especialidad").addEventListener("click", () => {
        $("#filtro-especialidad").reset();
        renderVeterinarios();
    });

    document.addEventListener("click", (e) => {
        const accion = e.target.closest("[data-accion]");
        if (accion) {
            manejarAccion(accion);
        }
        const experimento = e.target.closest("[data-experimento]");
        if (experimento) {
            conBoton(experimento, EXPERIMENTOS[experimento.dataset.experimento]);
        }
    });

    $("#limpiar-registro").addEventListener("click", () => {
        $("#lista-registro").innerHTML = "";
    });

    const minimo = new Date(Date.now() + 60 * 60000);
    $("#form-turno").elements.fechaHora.min =
        new Date(minimo.getTime() - minimo.getTimezoneOffset() * 60000).toISOString().slice(0, 16);

    const guardado = leerToken();
    const datos = guardado ? decodificarToken(guardado) : null;
    if (datos && datos.exp * 1000 > Date.now()) {
        entrar(guardado);
    }
}

iniciar();
