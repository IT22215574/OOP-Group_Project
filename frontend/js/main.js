// Renders a property card HTML string
function propertyCard(p) {
    const badge     = p.type === 'rent' ? 'For Rent' : 'For Sale';
    const badgeColor= p.type === 'rent' ? 'bg-blue-500' : 'bg-green-500';
    const price     = p.type === 'rent'
        ? `LKR ${Number(p.price).toLocaleString()}/mo`
        : `LKR ${Number(p.price).toLocaleString()}`;
    const img = p.imageUrl || `https://placehold.co/400x220/1a3c5e/ffffff?text=${encodeURIComponent(p.category || 'Property')}`;
// Renders a property card HTML string
/* INCOMING CHANGE COMMENTED OUT (merge conflict) BEGIN

INCOMING CHANGE COMMENTED OUT (merge conflict) END */
    return `
    <div class="bg-white rounded-2xl overflow-hidden shadow-sm hover:shadow-md transition group cursor-pointer"
         onclick="window.location.href='pages/property-detail.html?id=${p.id}'">
        <div class="relative overflow-hidden h-52">
            <img src="${img}" alt="${p.title}"
                 class="w-full h-full object-cover group-hover:scale-105 transition duration-300"
                 onerror="this.src='https://placehold.co/400x220/1a3c5e/ffffff?text=PrimeEstate'" />
            <span class="absolute top-3 left-3 text-xs text-white px-2 py-1 rounded-full font-semibold ${badgeColor}">${badge}</span>
        </div>
        <div class="p-5">
            <h3 class="text-primary font-bold text-base mb-1 truncate">${p.title}</h3>
            <p class="text-gray-400 text-xs mb-3 flex items-center gap-1">
                <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clip-rule="evenodd"/>
                </svg>
                ${p.city}${p.state ? ', ' + p.state : ''}
            </p>
            <div class="flex items-center gap-4 text-xs text-gray-500 mb-4 border-t pt-3">
                ${p.bedrooms  ? `<span>${p.bedrooms} Beds</span>` : ''}
                ${p.bathrooms ? `<span>${p.bathrooms} Baths</span>` : ''}
                ${p.areaSqft  ? `<span>${p.areaSqft.toLocaleString()} sqft</span>` : ''}
            </div>
            <div class="flex items-center justify-between">
                <span class="text-primary font-extrabold text-lg">${price}</span>
                <span class="text-xs text-gray-400 capitalize bg-gray-100 px-2 py-1 rounded-full">${p.category}</span>
            </div>
        </div>
    </div>`;
}

// Load featured properties on homepage
async function loadFeaturedProperties() {
    const container = document.getElementById('featured-properties');
    if (!container) return;

    try {
        const res = await api.getProperties({ limit: 6 });
        if (res.success && res.data.properties.length > 0) {
            container.innerHTML = res.data.properties.map(propertyCard).join('');
        } else {
            container.innerHTML = '<div class="col-span-full text-center py-10 text-gray-400">No properties found.</div>';
        }
    } catch (e) {
        container.innerHTML = '<div class="col-span-full text-center py-10 text-gray-400">Could not load properties — make sure the backend is running.</div>';
    }
}

function searchProperties() {
    const city     = document.getElementById('search-city')?.value.trim();
    const type     = document.getElementById('search-type')?.value;
    const category = document.getElementById('search-category')?.value;
    const params   = new URLSearchParams();
    if (city)     params.set('city', city);
    if (type)     params.set('type', type);
    if (category) params.set('category', category);
    window.location.href = `pages/properties.html?${params.toString()}`;
}

// Init
loadFeaturedProperties();
