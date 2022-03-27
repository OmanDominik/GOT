const MOUNTAIN_RANGE_SELECT = document.getElementById('mountainRange');

window.onload = function() {
    const TRAILS_URL = 'http://localhost:8080/trails';
    fetch(TRAILS_URL, {method: 'GET', headers: {'Content-Type': 'application/json'}})
        .then(resp => {return resp.json()}).then(data => {generateList(data)}).then(() => turnOffLoadingAnimation());

    const RANGES_URL = 'http://localhost:8080/ranges';
    fetch(RANGES_URL, {method: 'GET', headers: {'Content-Type': 'application/json'}})
        .then(resp => {return resp.json()}).then(data => {fillMountainRangesSelectList(data, MOUNTAIN_RANGE_SELECT)});

    const MOUNTAIN_POINTS_URL = 'http://localhost:8080/points';
    fetch(MOUNTAIN_POINTS_URL, {method: 'GET', headers: {'Content-Type': 'application/json'}})
        .then(resp => {return resp.json()}).then(data => {
        fillMountainPointsSelectList(data, document.querySelector('#startingPointsList'), true);
        fillMountainPointsSelectList(data, document.querySelector('#endPointsList'), true);
    })
};

function generateList(data) {
    const trailsNotFound = document.getElementById('trailsNotFound');
    if(trailsNotFound != null)
        trailsNotFound.remove();

    document.querySelector('table').innerHTML = `<tr>
            <th style="width: 40%">Punkt początkowy</th>
            <th style="width: 40%">Punkt końcowy</th>
            <th style="width: 7%">Kolor</th>
            <th style="width: 7%">Czas</th>
            <th style="width: 2%">Dwukierunkowy</th>
            <th style="width: 4%">Punkty</th>
        </tr>`;
    for (let i = 0; i < data.length; i++) {
        displayTrail(data[i]);
    }
}

function displayTrail(trail) {
    let isOneWayIcon;
    let isOneWayColor;
    if(trail["oneWay"] == true) {
        isOneWayIcon = `<i class="fas fa-times"></i>`;
        isOneWayColor = 'red';
    }
    else {
        isOneWayIcon = `<i class="fas fa-check"></i>`;
        isOneWayColor = 'green';
    }

    let pointsLabel;
    if(trail["pointsForDescent"] == null)
        pointsLabel = `${trail["pointsForReaching"]}`;
    else
        pointsLabel = `${trail["pointsForReaching"]}/${trail["pointsForDescent"]}`;

    let colorLabel = '';
    if(trail["color"] == 'BZ')
        colorLabel = 'BZ';

    document.querySelector('table').innerHTML += `<tr> <td>${trail["startingPoint"]["name"]}</td>
                                         <td>${trail["endPoint"]["name"]}</td> <td><span style="background-color: ${trail["color"]}" class="trailColorCircle">${colorLabel}</span></td>
                                         <td>${trail["estimatedTime"]}min</td><td style="text-align: center; color: ${isOneWayColor}">${isOneWayIcon}</td>
                                         <td>${pointsLabel}</td></tr>`;
}

function displayNotFoundTrailsMsg() {
    document.getElementById('list').innerHTML = `<p id="trailsNotFound">Nie znaleziono</p> <table></table>`;
}

const SEARCH_BTN = document.getElementById('searchBtn');
SEARCH_BTN.addEventListener('click', searchResults);
document.getElementById('searchInput').addEventListener('keyup', (ev) => {
   if(ev.key == 'Enter') {
       ev.preventDefault();
       searchResults();
   }
});

function searchResults() {
    const searchPhraseValue = document.getElementById('search').value;
    const startingPointValue = chosenStartingPoint;
    const endPointValue = chosenEndPoint;
    const mountainRangeValue = MOUNTAIN_RANGE_SELECT.value;
    const colorValue = document.getElementById('color').value;
    const minPointsValue = document.getElementById('minPoints').value;
    const maxPointsValue = document.getElementById('maxPoints').value;
    const minEstimatedTimeValue = document.getElementById('minEstimatedTime').value;
    const maxEstimatedTimeValue = document.getElementById('maxEstimatedTime').value;
    const URL = `http://localhost:8080/trails/filtered?search=${searchPhraseValue}&start=${startingPointValue}&end=${endPointValue}&range=${mountainRangeValue}&color=${colorValue}&minPoints=${minPointsValue}&maxPoints=${maxPointsValue}&minTime=${minEstimatedTimeValue}&maxTime=${maxEstimatedTimeValue}`;

    document.querySelector('table').innerHTML = '';
    turnOnLoadingAnimation();
    fetch(URL, {method: 'GET', headers: {'Content-Type': 'application/json'}})
        .then(resp => {return resp.json()}).then(data => {
        if(data.length > 0)
            generateList(data);
        else
            displayNotFoundTrailsMsg();
    }).then(() => turnOffLoadingAnimation());
}

const FILTER_BTN = document.getElementById('filterBtn');
FILTER_BTN.addEventListener('click', displayFiltersMenu);
function displayFiltersMenu() {
    document.getElementById('filters').classList.toggle('active');
}

const PICK_MOUNTAIN_POINT_BTNS = document.querySelectorAll('.pickMountainPoint');


PICK_MOUNTAIN_POINT_BTNS.forEach(btn => {
    btn.addEventListener('click', () => {
        displayMountainPointsPopUp();
    });
});

let chosenStartingPoint = '-';
let chosenEndPoint = '-';
const SAVE_BTN = document.getElementById('saveChosenPointsBtn');
SAVE_BTN.addEventListener('click', () => {
    chosenStartingPoint = document.getElementById('startingPointsList').value;
    chosenEndPoint = document.getElementById('endPointsList').value;

    if(chosenStartingPoint != '-') {
        document.getElementById('choosingStartingPointParagraph').innerHTML =
            `<button class="pickMountainPoint">Wybierz</button> Punkt początkowy: ${chosenStartingPoint} <span id="deleteChosenStartingPointIcon" class="icon"><i class="fas fa-times"></i></span>`;
        const deleteChosenStartingPointIcon = document.getElementById('deleteChosenStartingPointIcon');
        deleteChosenStartingPointIcon.addEventListener('click', () => {
            document.getElementById('choosingStartingPointParagraph').innerHTML =
                `<button class="pickMountainPoint">Wybierz</button> Punkt początkowy:`;
            chosenStartingPoint = '-';
            document.getElementById('startingPointsList').value = '-';
            addEventListenersToPickPointBtns();
        });
        addEventListenersToPickPointBtns();
    }
    if(chosenEndPoint != '-') {
        document.getElementById('choosingEndPointParagraph').innerHTML =
            `<button class="pickMountainPoint">Wybierz</button> Punkt końcowy: ${chosenEndPoint} <span id="deleteChosenEndPointIcon" class="icon"><i class="fas fa-times"></i></span>`;
        const deleteChosenEndPointIcon = document.getElementById('deleteChosenEndPointIcon');
        deleteChosenEndPointIcon.addEventListener('click', () => {
            document.getElementById('choosingEndPointParagraph').innerHTML =
                `<button class="pickMountainPoint">Wybierz</button> Punkt końcowy:`;
            chosenEndPoint = '-';
            document.getElementById('endPointsList').value = '-';
            addEventListenersToPickPointBtns();
        });
        addEventListenersToPickPointBtns();
    }
    turnOffPopup();
});

function addEventListenersToPickPointBtns() {
    document.querySelectorAll('.pickMountainPoint').forEach(btn => {
        btn.addEventListener('click', () => {
            displayMountainPointsPopUp();
        });
    });
}

function turnOffLoadingAnimation() {
    document.getElementById('loadAnim').style.display = 'none';
}

function turnOnLoadingAnimation() {
    document.getElementById('loadAnim').style.display = 'inline-block';
}

