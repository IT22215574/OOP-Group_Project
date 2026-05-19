// Checks session and updates navbar auth links
async function initAuth() {
    const user = sessionStorage.getItem('user');
    if (!user) return;

    const parsed  = JSON.parse(user);
    const navAuth = document.getElementById('nav-auth');
    if (!navAuth) return;

    navAuth.innerHTML = `
        <span class="text-sm text-gray-200">Hi, ${parsed.fullName.split(' ')[0]}</span>
        <button onclick="logout()"
                class="text-sm border border-white px-4 py-1.5 rounded-full hover:bg-white hover:text-primary transition">
            Logout
        </button>`;
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
