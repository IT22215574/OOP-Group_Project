let currentPage = 1;
const urlParams = new URLSearchParams(window.location.search);

// Pre-fill filters from URL
document.getElementById('filter-type').value     = urlParams.get('type')     || '';
document.getElementById('filter-category').value = urlParams.get('category') || '';
document.getElementById('filter-city').value     = urlParams.get('city')     || '';

function buildParams(page) {
    const params = { page };
    const city     = document.getElementById('filter-city').value.trim();
    const type     = document.getElementById('filter-type').value;
    const category = document.getElementById('filter-category').value;
    if (city)     params.city     = city;
    if (type)     params.type     = type;
    if (category) params.category = category;
    return params;
}

async function loadProperties(page = 1) {
    currentPage = page;
    const grid = document.getElementById('property-grid');
    const info = document.getElementById('results-info');
    grid.innerHTML = '<div class="col-span-full text-center py-16 text-gray-400">Loading...</div>';

    try {
        const res = await api.getProperties(buildParams(page));
        if (!res.success) throw new Error(res.message);

        const { properties, total, pages } = res.data;
        info.textContent = `${total} ${total === 1 ? 'property' : 'properties'} found`;

        if (properties.length === 0) {
            grid.innerHTML = '<div class="col-span-full text-center py-16 text-gray-400">No properties match your search.</div>';
            document.getElementById('pagination').innerHTML = '';
            return;
        }

        grid.innerHTML = properties.map(p => {
            const badge      = p.type === 'rent' ? 'For Rent' : 'For Sale';
            const badgeColor = p.type === 'rent' ? 'bg-blue-500' : 'bg-green-500';
            const price      = p.type === 'rent'
                ? `LKR ${Number(p.price).toLocaleString()}/mo`
                : `LKR ${Number(p.price).toLocaleString()}`;
            const img = p.imageUrl || `https://placehold.co/400x220/1a3c5e/ffffff?text=${encodeURIComponent(p.category || 'Property')}`;

            return `
            <div class="bg-white rounded-2xl overflow-hidden shadow-sm hover:shadow-md transition group cursor-pointer"
                 onclick="window.location.href='property-detail.html?id=${p.id}'">
                <div class="relative overflow-hidden h-52">
                    <img src="${img}" alt="${p.title}"
                         class="w-full h-full object-cover group-hover:scale-105 transition duration-300"
                         onerror="this.src='https://placehold.co/400x220/1a3c5e/ffffff?text=PrimeEstate'" />
                    <span class="absolute top-3 left-3 text-xs text-white px-2 py-1 rounded-full font-semibold ${badgeColor}">${badge}</span>
                </div>
                <div class="p-5">
                    <h3 class="text-primary font-bold text-base mb-1 truncate">${p.title}</h3>
                    <p class="text-gray-400 text-xs mb-3">${p.city}${p.state ? ', ' + p.state : ''}</p>
                    <div class="flex items-center gap-4 text-xs text-gray-500 mb-4 border-t pt-3">
                        ${p.bedrooms  ? `<span>${p.bedrooms} Beds</span>`  : ''}
                        ${p.bathrooms ? `<span>${p.bathrooms} Baths</span>` : ''}
                        ${p.areaSqft  ? `<span>${p.areaSqft.toLocaleString()} sqft</span>` : ''}
                    </div>
                    <div class="flex items-center justify-between">
                        <span class="text-primary font-extrabold text-lg">${price}</span>
                        <span class="text-xs text-gray-400 capitalize bg-gray-100 px-2 py-1 rounded-full">${p.category}</span>
                    </div>
                </div>
            </div>`;
        }).join('');

        renderPagination(pages, page);

    } catch (e) {
        grid.innerHTML = '<div class="col-span-full text-center py-16 text-gray-400">Could not load properties — make sure the backend server is running.</div>';
        info.textContent = '';
    }
}

function renderPagination(pages, current) {
    const container = document.getElementById('pagination');
    if (pages <= 1) { container.innerHTML = ''; return; }

    let html = '';
    for (let i = 1; i <= pages; i++) {
        const active = i === current
            ? 'bg-primary text-white'
            : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50';
        html += `<button onclick="loadProperties(${i})" class="px-4 py-2 rounded-lg text-sm font-medium transition ${active}">${i}</button>`;
    }
    container.innerHTML = html;
}

function applyFilters() { loadProperties(1); }

function resetFilters() {
    document.getElementById('filter-city').value     = '';
    document.getElementById('filter-type').value     = '';
    document.getElementById('filter-category').value = '';
    loadProperties(1);
}

// Init
loadProperties(1);
