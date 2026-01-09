const API_BASE_URL = '/api';

// DOM Elements
const urlForm = document.getElementById('urlForm');
const urlInput = document.getElementById('urlInput');
const shortenBtn = document.getElementById('shortenBtn');
const result = document.getElementById('result');
const shortUrlInput = document.getElementById('shortUrl');
const copyBtn = document.getElementById('copyBtn');
const successMessage = document.getElementById('successMessage');
const errorDiv = document.getElementById('error');
const urlsList = document.getElementById('urlsList');
const refreshBtn = document.getElementById('refreshBtn');

// Event Listeners
urlForm.addEventListener('submit', handleSubmit);
copyBtn.addEventListener('click', copyToClipboard);
refreshBtn.addEventListener('click', loadUrls);

// Load URLs on page load
document.addEventListener('DOMContentLoaded', () => {
    loadUrls();
});

async function handleSubmit(e) {
    e.preventDefault();
    
    const url = urlInput.value.trim();
    
    if (!url) {
        showError('Please enter a URL');
        return;
    }

    // Hide previous results
    result.classList.add('hidden');
    errorDiv.classList.add('hidden');
    
    // Disable button and show loading state
    shortenBtn.disabled = true;
    shortenBtn.textContent = 'Shortening...';

    try {
        const response = await fetch(`${API_BASE_URL}/shorten`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ url: url })
        });

        const data = await response.json();

        if (response.ok) {
            // Show result
            const baseUrl = window.location.origin;
            shortUrlInput.value = data.shortUrl || `${baseUrl}/${data.shortCode}`;
            result.classList.remove('hidden');
            urlInput.value = '';
            
            // Reload URLs list
            loadUrls();
        } else {
            showError(data.error || 'Failed to shorten URL');
        }
    } catch (error) {
        showError('Network error. Please try again.');
        console.error('Error:', error);
    } finally {
        shortenBtn.disabled = false;
        shortenBtn.textContent = 'Shorten';
    }
}

async function loadUrls() {
    urlsList.innerHTML = '<p class="loading">Loading...</p>';

    try {
        const response = await fetch(`${API_BASE_URL}/urls`);
        const urls = await response.json();

        if (urls.length === 0) {
            urlsList.innerHTML = '<p class="empty-state">No URLs shortened yet. Create your first shortened URL above!</p>';
            return;
        }

        const baseUrl = window.location.origin;
        urlsList.innerHTML = urls.map(url => `
            <div class="url-item">
                <div class="original-url">${escapeHtml(url.originalUrl)}</div>
                <div class="short-url">${escapeHtml(url.shortUrl || `${baseUrl}/${url.shortCode}`)}</div>
                <div class="meta">
                    <span>Created: ${formatDate(url.createdAt)}</span>
                    <span>Clicks: ${url.clickCount || 0}</span>
                </div>
            </div>
        `).join('');
    } catch (error) {
        urlsList.innerHTML = '<p class="error">Failed to load URLs. Please refresh the page.</p>';
        console.error('Error loading URLs:', error);
    }
}

function copyToClipboard() {
    shortUrlInput.select();
    shortUrlInput.setSelectionRange(0, 99999); // For mobile devices

    try {
        document.execCommand('copy');
        
        // Show success message
        successMessage.classList.remove('hidden');
        copyBtn.textContent = 'Copied!';
        
        setTimeout(() => {
            successMessage.classList.add('hidden');
            copyBtn.textContent = 'Copy';
        }, 2000);
    } catch (err) {
        // Fallback for modern browsers
        navigator.clipboard.writeText(shortUrlInput.value).then(() => {
            successMessage.classList.remove('hidden');
            copyBtn.textContent = 'Copied!';
            
            setTimeout(() => {
                successMessage.classList.add('hidden');
                copyBtn.textContent = 'Copy';
            }, 2000);
        }).catch(err => {
            console.error('Failed to copy:', err);
            alert('Failed to copy to clipboard');
        });
    }
}

function showError(message) {
    errorDiv.textContent = message;
    errorDiv.classList.remove('hidden');
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
}
