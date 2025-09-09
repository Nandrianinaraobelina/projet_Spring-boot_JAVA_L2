/**
 * Main JavaScript file for Musique application
 */

document.addEventListener('DOMContentLoaded', function() {
    // Initialize Feather Icons
    if (window.feather) {
        feather.replace();
    }
    
    // Enable Bootstrap tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Corrige le problème de navigation vers la page "Récent"
    const latestLink = document.getElementById('latestLink');
    if (latestLink) {
        latestLink.addEventListener('click', function(e) {
            // Pas besoin de preventDefault() ou redirection manuelle, 
            // puisque nous utilisons maintenant la route /equipment/latest
        });
    }
    
    // Enable Bootstrap popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    
    // Equipment catalog image error handling
    const equipmentImages = document.querySelectorAll('img[src^="https://cdn.feather.icons/"]');
    equipmentImages.forEach(img => {
        img.onerror = function() {
            // Replace with a default SVG icon
            const iconName = img.src.split('/').pop().replace('.svg', '');
            const parent = img.parentElement;
            
            // Remove the broken image
            img.remove();
            
            // Create a div with the Feather icon
            const iconContainer = document.createElement('div');
            iconContainer.className = 'bg-light rounded d-flex align-items-center justify-content-center';
            iconContainer.style.height = '150px';
            
            // Create the SVG wrapper
            const iconWrapper = document.createElement('i');
            iconWrapper.setAttribute('data-feather', iconName || 'music');
            iconWrapper.style.width = '48px';
            iconWrapper.style.height = '48px';
            iconWrapper.style.color = '#adb5bd';
            
            // Append elements
            iconContainer.appendChild(iconWrapper);
            parent.appendChild(iconContainer);
            
            // Initialize the Feather icon
            if (window.feather) {
                feather.replace();
            }
        };
    });
    
    // Equipment quantity selector
    const quantityInputs = document.querySelectorAll('input[type="number"][name="quantity"]');
    quantityInputs.forEach(input => {
        // Set minimum value
        input.min = 1;
        
        // Add event listeners for buttons
        const decrementBtn = input.previousElementSibling;
        const incrementBtn = input.nextElementSibling;
        
        if (decrementBtn && decrementBtn.classList.contains('btn-decrement')) {
            decrementBtn.addEventListener('click', function() {
                if (input.value > input.min) {
                    input.value = parseInt(input.value) - 1;
                    input.dispatchEvent(new Event('change'));
                }
            });
        }
        
        if (incrementBtn && incrementBtn.classList.contains('btn-increment')) {
            incrementBtn.addEventListener('click', function() {
                const max = input.max ? parseInt(input.max) : 99;
                if (parseInt(input.value) < max) {
                    input.value = parseInt(input.value) + 1;
                    input.dispatchEvent(new Event('change'));
                }
            });
        }
    });
    
    // Date validators for rental forms
    const startDateInputs = document.querySelectorAll('input[type="date"][name="rentalStartDate"]');
    const endDateInputs = document.querySelectorAll('input[type="date"][name="rentalEndDate"]');
    
    startDateInputs.forEach(startInput => {
        // Set min date to today
        const today = new Date();
        const todayFormatted = today.toISOString().split('T')[0];
        startInput.min = todayFormatted;
        
        // If no date is selected, default to today
        if (!startInput.value) {
            startInput.value = todayFormatted;
        }
        
        // Find the corresponding end date input
        const form = startInput.closest('form');
        if (form) {
            const endInput = form.querySelector('input[type="date"][name="rentalEndDate"]');
            if (endInput) {
                // Set min date for end date to start date
                startInput.addEventListener('change', function() {
                    endInput.min = startInput.value;
                    
                    // If end date is before start date, set it to start date
                    if (endInput.value && endInput.value < startInput.value) {
                        endInput.value = startInput.value;
                    }
                    
                    // If no end date is selected, default to start date + 1 day
                    if (!endInput.value) {
                        const nextDay = new Date(startInput.value);
                        nextDay.setDate(nextDay.getDate() + 1);
                        const nextDayFormatted = nextDay.toISOString().split('T')[0];
                        endInput.value = nextDayFormatted;
                    }
                });
                
                // Set initial min value
                endInput.min = startInput.value || todayFormatted;
            }
        }
    });
    
    // Add loading indicators to forms
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function() {
            const submitButton = form.querySelector('button[type="submit"]');
            if (submitButton) {
                // Save original content
                if (!submitButton.dataset.originalContent) {
                    submitButton.dataset.originalContent = submitButton.innerHTML;
                }
                
                // Show loading spinner
                submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Loading...';
                submitButton.disabled = true;
            }
        });
    });
    
    // Admin dashboard charts (if Chart.js is available)
    if (window.Chart && document.getElementById('salesChart')) {
        const salesCtx = document.getElementById('salesChart').getContext('2d');
        new Chart(salesCtx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                datasets: [{
                    label: 'Sales',
                    data: [12, 19, 3, 5, 2, 3],
                    borderColor: '#4361ee',
                    tension: 0.1
                }, {
                    label: 'Rentals',
                    data: [7, 11, 5, 8, 3, 7],
                    borderColor: '#38b000',
                    tension: 0.1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: 'Sales & Rentals'
                    }
                }
            }
        });
    }
    
    // Search functionality for equipment catalog
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = searchInput.value.toLowerCase();
            const equipmentCards = document.querySelectorAll('.col .card');
            
            equipmentCards.forEach(card => {
                const title = card.querySelector('.card-title').textContent.toLowerCase();
                const description = card.querySelector('.card-text').textContent.toLowerCase();
                
                if (title.includes(searchTerm) || description.includes(searchTerm)) {
                    card.closest('.col').style.display = '';
                } else {
                    card.closest('.col').style.display = 'none';
                }
            });
        });
    }
});

/**
 * Current currency setting
 */
let currentCurrency = localStorage.getItem('currency') || 'EUR';

/**
 * Currency conversion rates (simplified example)
 */
const currencyRates = {
    'EUR': 1,        // Euro (base currency)
    'MGA': 4800,     // Malagasy Ariary (approximation)
};

/**
 * Currency symbols
 */
const currencySymbols = {
    'EUR': '€',
    'MGA': 'Ar',
};

/**
 * Switch currency display
 * @param {string} currency - Currency code to switch to (EUR or MGA)
 */
function switchCurrency(currency) {
    if (!currencyRates[currency]) return;
    
    currentCurrency = currency;
    localStorage.setItem('currency', currency);
    
    // Update all price elements on the page
    const priceElements = document.querySelectorAll('[data-price]');
    priceElements.forEach(el => {
        const basePrice = parseFloat(el.dataset.price);
        if (!isNaN(basePrice)) {
            el.textContent = formatCurrency(basePrice);
        }
    });
    
    // Update currency switcher UI
    const currencySwitchers = document.querySelectorAll('.currency-switcher');
    currencySwitchers.forEach(switcher => {
        const buttons = switcher.querySelectorAll('button');
        buttons.forEach(btn => {
            if (btn.dataset.currency === currency) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });
    });
}

/**
 * Format currency according to current settings
 * @param {number} amount - The amount to format in base currency (EUR)
 * @returns {string} Formatted currency string
 */
function formatCurrency(amount) {
    if (!amount) return currencySymbols[currentCurrency] + '0.00';
    
    // Convert to current currency
    const convertedAmount = parseFloat(amount) * currencyRates[currentCurrency];
    
    // Format with correct decimal places (0 for MGA, 2 for others)
    if (currentCurrency === 'MGA') {
        return currencySymbols[currentCurrency] + Math.round(convertedAmount).toLocaleString();
    } else {
        return currencySymbols[currentCurrency] + convertedAmount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
    }
}

// Initialize currency switcher UI when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Create currency switcher if it doesn't exist
    if (!document.querySelector('.currency-switcher') && document.querySelector('footer')) {
        const currencySwitcher = document.createElement('div');
        currencySwitcher.className = 'currency-switcher position-fixed bottom-0 end-0 mb-4 me-4 bg-white shadow rounded p-2';
        
        const euroBtn = document.createElement('button');
        euroBtn.className = 'btn btn-sm me-1 ' + (currentCurrency === 'EUR' ? 'btn-primary' : 'btn-outline-primary');
        euroBtn.textContent = '€ EUR';
        euroBtn.dataset.currency = 'EUR';
        euroBtn.addEventListener('click', () => switchCurrency('EUR'));
        
        const ariaryBtn = document.createElement('button');
        ariaryBtn.className = 'btn btn-sm ' + (currentCurrency === 'MGA' ? 'btn-primary' : 'btn-outline-primary');
        ariaryBtn.textContent = 'Ar MGA';
        ariaryBtn.dataset.currency = 'MGA';
        ariaryBtn.addEventListener('click', () => switchCurrency('MGA'));
        
        currencySwitcher.appendChild(euroBtn);
        currencySwitcher.appendChild(ariaryBtn);
        
        document.body.appendChild(currencySwitcher);
    }
    
    // Add data-price attributes to all price elements
    document.querySelectorAll('td, span, div').forEach(el => {
        const text = el.textContent.trim();
        if (text.startsWith('€')) {
            const priceValue = parseFloat(text.replace('€', '').replace(',', ''));
            if (!isNaN(priceValue)) {
                el.dataset.price = priceValue;
                el.textContent = formatCurrency(priceValue);
            }
        }
    });
});

/**
 * Format date in DD-MM-YYYY format
 * @param {string|Date} date - The date to format
 * @returns {string} Formatted date string
 */
function formatDate(date) {
    const d = new Date(date);
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();
    return `${day}-${month}-${year}`;
}

/**
 * Calculate days between two dates (inclusive)
 * @param {string|Date} startDate - Start date
 * @param {string|Date} endDate - End date
 * @returns {number} Number of days
 */
function calculateDays(startDate, endDate) {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const diffTime = Math.abs(end - start);
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; // Including both start and end days
}
