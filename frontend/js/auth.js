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
        const myPropHref = resolveMyPropertiesPath();
        extraLinks = `
            <a href="${apptHref}"  class="text-sm text-accent hover:text-accent-light transition font-medium">Appointments</a>
            <a href="${myPropHref}" class="text-sm text-accent hover:text-accent-light transition font-medium">My Listings</a>
            <a href="${postHref}"  class="text-sm text-accent hover:text-accent-light transition font-medium">Post Property</a>`;
    } else if (parsed.role === 'buyer') {
        const apptHref = resolveUserAppointmentsPath();
        extraLinks = `<a href="${apptHref}" class="text-sm text-accent hover:text-accent-light transition font-medium">My Appointments</a>`;
    }

    const profileHref = resolveProfilePath();
    const initials    = parsed.fullName.trim().split(/\s+/).map(n => n[0]).join('').toUpperCase().slice(0, 2);

    navAuth.innerHTML = `
        ${extraLinks}
        <span class="text-sm text-gray-200">Hi, ${parsed.fullName.split(' ')[0]}</span>
        <a href="${profileHref}" title="My Profile"
           class="w-8 h-8 rounded-full bg-white/20 hover:bg-white/30 flex items-center justify-center text-white text-xs font-bold transition flex-shrink-0">
            ${initials}
        </a>
        <button onclick="logout()"
                class="text-sm border border-white px-4 py-1.5 rounded-full hover:bg-white hover:text-primary transition">
            Logout
        </button>`;
}

function resolveProfilePath() {
    const path = window.location.pathname;
    if (path.includes('/pages/admin/')) return '../profile.html';
    if (path.includes('/pages/'))       return 'profile.html';
    return 'pages/profile.html';
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

function resolveMyPropertiesPath() {
    const path = window.location.pathname;
    if (path.includes('/pages/admin/')) return '../my-properties.html';
    if (path.includes('/pages/'))       return 'my-properties.html';
    return 'pages/my-properties.html';
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
