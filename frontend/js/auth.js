// Checks session and updates navbar auth links
async function initAuth() {
    const user = sessionStorage.getItem('user');
    if (!user) return;

    const parsed  = JSON.parse(user);
    const navAuth = document.getElementById('nav-auth');
    if (!navAuth) return;

    // Build role-specific links
    let extraLinks = '';
    if (parsed.role === 'admin') {
        const adminHref = resolveAdminPath();
        extraLinks = `<a href="${adminHref}" class="text-sm text-accent hover:text-accent-light transition font-medium">Admin Panel</a>`;
    } else if (parsed.role === 'agent') {
        const postHref  = resolvePostPropertyPath();
        const apptHref  = resolveAgentAppointmentsPath();
        extraLinks = `
            <a href="${apptHref}" class="text-sm text-accent hover:text-accent-light transition font-medium">My Appointments</a>
            <a href="${postHref}" class="text-sm text-accent hover:text-accent-light transition font-medium">Post Property</a>`;
    } else if (parsed.role === 'buyer') {
        const apptHref = resolveUserAppointmentsPath();
        extraLinks = `<a href="${apptHref}" class="text-sm text-accent hover:text-accent-light transition font-medium">My Appointments</a>`;
    }

    navAuth.innerHTML = `
        ${extraLinks}
        <span class="text-sm text-gray-200">Hi, ${parsed.fullName.split(' ')[0]}</span>
        <button onclick="logout()"
                class="text-sm border border-white px-4 py-1.5 rounded-full hover:bg-white hover:text-primary transition">
            Logout
        </button>`;
}

function resolveAdminPath() {
    const path = window.location.pathname;
    if (path.includes('/pages/admin/')) return 'dashboard.html';
    if (path.includes('/pages/'))       return 'admin/dashboard.html';
    return 'pages/admin/dashboard.html';
}

function resolvePostPropertyPath() {
    const path = window.location.pathname;
    if (path.includes('/pages/admin/')) return '../post-property.html';
    if (path.includes('/pages/'))       return 'post-property.html';
    return 'pages/post-property.html';
}

function resolveUserAppointmentsPath() {
    const path = window.location.pathname;
    if (path.includes('/pages/admin/')) return '../appointments.html';
    if (path.includes('/pages/'))       return 'appointments.html';
    return 'pages/appointments.html';
}

function resolveAgentAppointmentsPath() {
    const path = window.location.pathname;
    if (path.includes('/pages/admin/')) return '../agent-appointments.html';
    if (path.includes('/pages/'))       return 'agent-appointments.html';
    return 'pages/agent-appointments.html';
}

async function logout() {
    if (!window.confirm('Are you sure you want to log out?')) return;
    try { await api.logout(); } catch (_) {}
    sessionStorage.removeItem('user');
    const basePath = window.location.pathname.split('/frontend/')[0];
    const homePath = `${basePath}/frontend/index.html`;
    window.location.href = homePath;
}

initAuth();
