const SHOW_TRAILS_BTN = document.querySelector('nav:nth-of-type(1) .option:nth-child(1)');
SHOW_TRAILS_BTN.addEventListener('click', () => {
    location.href = 'trails.html';
});

const SHOW_ROUTE_GENERATOR_BTN = document.querySelector('nav:nth-of-type(1) .option:nth-child(2)');
SHOW_ROUTE_GENERATOR_BTN.addEventListener('click', () => {
    location.href = 'route-generator.html'
});

const LOGIN_OPTION = document.querySelector('nav:nth-of-type(2) .option:nth-child(1)');
LOGIN_OPTION.addEventListener('click', () => {
    location.href = '/admin-panel.html';
});

const LOGO = document.querySelector('#logo img');
LOGO.addEventListener('click', () => {
    location.href = 'index.html'
});

