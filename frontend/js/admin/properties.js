let currentPage = 1;

async function requireAdmin() {
    try {
        const res = await api.getMe();
        if (!res.success || res.data.role !== 'admin') window.location.href = '../../pages/login.html';
        document.getElementById('admin-name').textContent = res.data.fullName;
    } catch (_) { window.location.href = '../../pages/login.html'; }
}

async function loadProperties(page = 1) {
    currentPage = page;
    const city     = document.getElementById('filter-city').value.trim();
    const type     = document.getElementById('filter-type').value;
    const category = document.getElementById('filter-category').value;

    const params = { page, limit: 15 };
    if (city)     params.city     = city;
    if (type)     params.type     = type;
    if (category) params.category = category;

    const tbody = document.getElementById('props-tbody');
    tbody.innerHTML = '<tr><td colspan="8" class="px-6 py-10 text-center text-gray-400">Loading...</td></tr>';

    try {
        const res = await api.getProperties(params);
        if (!res.success) throw new Error(res.message);
        renderProperties(res.data.properties || []);
        renderPagination(res.data.pages || 1, page);
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="8" class="px-6 py-10 text-center text-red-400">${e.message}</td></tr>`;
    }
}

function renderProperties(props) {
    const tbody = document.getElementById('props-tbody');
    if (!props.length) {
        tbody.innerHTML = '<tr><td colspan="8" class="px-6 py-10 text-center text-gray-400">No properties found</td></tr>';
        return;
    }
    tbody.innerHTML = props.map(p => `
        <tr class="hover:bg-gray-50">
            <td class="px-6 py-3 text-gray-400 text-xs">#${p.id}</td>
            <td class="px-6 py-3 font-medium text-gray-800 max-w-[200px] truncate">${p.title}</td>
            <td class="px-6 py-3">${typeBadge(p.type)}</td>
            <td class="px-6 py-3 text-gray-500 capitalize">${p.category || '—'}</td>
            <td class="px-6 py-3 text-gray-500">${p.city || '—'}</td>
            <td class="px-6 py-3 text-gray-800 font-medium">LKR ${Number(p.price).toLocaleString()}</td>
            <td class="px-6 py-3">${propStatusBadge(p.status)}</td>
            <td class="px-6 py-3">
                <div class="flex gap-2">
                    <a href="../property-detail.html?id=${p.id}"
                       class="text-xs border border-gray-200 text-gray-600 px-3 py-1.5 rounded-lg hover:bg-gray-50 transition">View</a>
                    <button onclick='openEdit(${JSON.stringify(p)})'
                            class="text-xs bg-primary text-white px-3 py-1.5 rounded-lg hover:bg-primary-dark transition">Edit</button>
                    <button onclick="deleteProp(${p.id}, '${p.title.replace(/'/g,'')}')"
                            class="text-xs bg-red-50 text-red-600 border border-red-200 px-3 py-1.5 rounded-lg hover:bg-red-100 transition">Delete</button>
                </div>
            </td>
        </tr>`).join('');
}

function renderPagination(totalPages, current) {
    const el = document.getElementById('pagination');
    if (totalPages <= 1) { el.innerHTML = ''; return; }
    let html = '';
    for (let i = 1; i <= totalPages; i++) {
        html += `<button onclick="loadProperties(${i})"
                         class="px-3 py-1.5 rounded-lg text-sm ${i === current ? 'bg-primary text-white' : 'border border-gray-200 text-gray-600 hover:bg-gray-50'}">${i}</button>`;
    }
    el.innerHTML = html;
}

function openEdit(prop) {
    document.getElementById('edit-id').value       = prop.id;
    document.getElementById('edit-title').value    = prop.title;
    document.getElementById('edit-type').value     = prop.type || 'sale';
    document.getElementById('edit-category').value = prop.category || 'house';
    document.getElementById('edit-price').value    = prop.price;
    document.getElementById('edit-status').value   = prop.status || 'available';
    document.getElementById('edit-city').value     = prop.city || '';
    document.getElementById('edit-bedrooms').value = prop.bedrooms || 0;
    document.getElementById('modal-alert').classList.add('hidden');
    document.getElementById('edit-modal').classList.remove('hidden');
}

function closeModal() {
    document.getElementById('edit-modal').classList.add('hidden');
}

document.getElementById('edit-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('edit-id').value;
    const data = {
        title:    document.getElementById('edit-title').value.trim(),
        type:     document.getElementById('edit-type').value,
        category: document.getElementById('edit-category').value,
        price:    parseFloat(document.getElementById('edit-price').value),
        status:   document.getElementById('edit-status').value,
        city:     document.getElementById('edit-city').value.trim(),
        bedrooms: parseInt(document.getElementById('edit-bedrooms').value) || 0,
    };
    try {
        const res = await api.updateProperty(id, data);
        if (res.success) { closeModal(); loadProperties(currentPage); }
        else showModalAlert(res.message || 'Update failed', 'error');
    } catch (_) { showModalAlert('Connection error', 'error'); }
});

async function deleteProp(id, title) {
    if (!confirm(`Delete property "${title}"? This cannot be undone.`)) return;
    try {
        const res = await api.deleteProperty(id);
        if (res.success) loadProperties(currentPage);
        else alert(res.message || 'Delete failed');
    } catch (_) { alert('Connection error'); }
}

function resetFilters() {
    document.getElementById('filter-city').value     = '';
    document.getElementById('filter-type').value     = '';
    document.getElementById('filter-category').value = '';
    loadProperties(1);
}

function showModalAlert(msg, type) {
    const el = document.getElementById('modal-alert');
    el.textContent = msg;
    el.className = `mb-4 p-3 rounded-lg text-sm ${type === 'error' ? 'bg-red-50 text-red-700 border border-red-200' : 'bg-green-50 text-green-700 border border-green-200'}`;
    el.classList.remove('hidden');
}

function typeBadge(type) {
    return type === 'sale'
        ? '<span class="px-2 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-700">Sale</span>'
        : '<span class="px-2 py-0.5 rounded-full text-xs font-medium bg-orange-100 text-orange-700">Rent</span>';
}

function propStatusBadge(status) {
    const map = { available: 'bg-green-100 text-green-700', sold: 'bg-gray-100 text-gray-600', rented: 'bg-yellow-100 text-yellow-700' };
    return `<span class="px-2 py-0.5 rounded-full text-xs font-medium ${map[status] || 'bg-gray-100 text-gray-500'} capitalize">${status || '—'}</span>`;
}

function adminLogout() {
    api.logout().catch(() => {});
    sessionStorage.removeItem('user');
    window.location.href = '../../pages/login.html';
}

requireAdmin().then(() => loadProperties());
