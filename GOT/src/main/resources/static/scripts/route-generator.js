window.onload = function() {
    const RANGES_URL = 'http://localhost:8080/ranges';
    fetch(RANGES_URL, {method: 'GET', headers: {'Content-Type': 'application/json'}})
        .then(resp => {return resp.json()})
        .then(data => {
            fillMountainRangesSelectListAndSetDeafultSettings(data, document.getElementById('startingPointMountainRange'));
            fillMountainRangesSelectListAndSetDeafultSettings(data, document.getElementById('endPointMountainRange'));
            fillMountainRangesSelectListAndSetDeafultSettings(data, document.getElementById('intermediatePointMountainRange'));
        }
        ).then(() => {
        updateMountainPointList('startingPointMountainRange', 'startingPointsList');
        updateMountainPointList('endPointMountainRange', 'endPointsList');
        updateMountainPointList('intermediatePointMountainRange', 'intermediatePointsList');
    });
};

const PICK_MOUNTAIN_POINT_BTNS = document.querySelectorAll('.pickMountainPoint');


PICK_MOUNTAIN_POINT_BTNS.forEach(btn => {
    btn.addEventListener('click', () => {
        displayMountainPointsPopUp();
    });
});

const PICK_STARTING_POINT = document.getElementById('pickStartingPoint');
function displayStartingPointPopup() {
    document.getElementById('pickStartingMountainPointPopUp').classList.add('active');
    document.getElementById('overlay').classList.add('active');
}

const ADD_INTERMEDIATE_POINT = document.getElementById('addIntermediatePoint');
function displayIntermediatePointsPopup() {
    document.getElementById('pickIntermediateMountainPointPopUp').classList.add('active');
    document.getElementById('overlay').classList.add('active');
}

const PICK_END_POINT = document.getElementById('pickEndPoint');
function displayEndPointPopup() {
    document.getElementById('pickEndMountainPointPopUp').classList.add('active');
    document.getElementById('overlay').classList.add('active');
}

PICK_STARTING_POINT.addEventListener('click', displayStartingPointPopup);
ADD_INTERMEDIATE_POINT.addEventListener('click', displayIntermediatePointsPopup);
PICK_END_POINT.addEventListener('click', displayEndPointPopup)


document.getElementById('overlay').addEventListener('click', turnOffPopups);
function turnOffPopups() {
    document.getElementById('pickStartingMountainPointPopUp').classList.remove('active');
    document.getElementById('pickIntermediateMountainPointPopUp').classList.remove('active');
    document.getElementById('pickEndMountainPointPopUp').classList.remove('active');
    document.getElementById('overlay').classList.remove('active');
}

const CLOSE_POPUP_ICONS = document.querySelectorAll('.closePopupIcon');
CLOSE_POPUP_ICONS.forEach(icon => {
   icon.addEventListener('click', turnOffPopups);
});

const CHOSEN_STARTING_POINT_ELEM = document.getElementById('chosenStartingPoint');
const CHOSEN_INTERMEDIATE_POINTS_ELEM = document.getElementById('chosenIntermediatePoints');
const CHOSEN_END_POINT_ELEM = document.getElementById('chosenEndPoint');

const SAVE_SELECTED_STARTING_POINT_BTN = document.getElementById('saveSelectedStartingPointBtn');
const SAVE_SELECTED_INTERMEDIATE_POINT_BTN = document.getElementById('saveSelectedIntermediatePointBtn');
const SAVE_SELECTED_END_POINT_BTN = document.getElementById('saveSelectedEndPointBtn');

let startingPoint;
let endPoint;
let intermediatePoints = new Set();
SAVE_SELECTED_STARTING_POINT_BTN.addEventListener('click', () => {
    startingPoint = document.getElementById('startingPointsList').value;
    CHOSEN_STARTING_POINT_ELEM.innerHTML = `<p><i class="far fa-flag"></i>  ${startingPoint}</p>`;
    turnOffPopups();
    if(startingPoint != null && endPoint != null)
        allowGeneration();
    else
        disallowGeneration();
});

SAVE_SELECTED_END_POINT_BTN.addEventListener('click', () => {
    endPoint = document.getElementById('endPointsList').value;
    CHOSEN_END_POINT_ELEM.innerHTML = `<p><i class="fas fa-flag-checkered"></i> ${endPoint}</p>`;
    turnOffPopups();
    if(startingPoint != null && endPoint != null)
        allowGeneration();
    else
        disallowGeneration();

});

SAVE_SELECTED_INTERMEDIATE_POINT_BTN.addEventListener('click', () => {
    let decider = true;
    let intermediatePoint;
    intermediatePoint = document.getElementById('intermediatePointsList').value;
    intermediatePoints.forEach(point => {
        if(point == intermediatePoint) {
            decider = false;
        }
    })
    if(decider) {
        intermediatePoints.add(intermediatePoint);
        let intermediatePointElement = document.createElement('p');
        intermediatePointElement.innerHTML = `<i class="fas fa-flag" style="color: #377ccb"></i> ${intermediatePoint}   `;
        let closeIcon = document.createElement('i');
        closeIcon.classList.add('fas', 'fa-times', 'deleteIntermediatePoint');
        closeIcon.addEventListener('click', () => {
            intermediatePointElement.remove();
            intermediatePoints.delete(intermediatePoint);
        });
        intermediatePointElement.appendChild(closeIcon);

        CHOSEN_INTERMEDIATE_POINTS_ELEM.appendChild(intermediatePointElement);
    }
    turnOffPopups();
});

const GENERATE_BTN = document.getElementById('generateBtn');

function allowGeneration() {
    GENERATE_BTN.classList.add('active');
}

function disallowGeneration() {
    GENERATE_BTN.classList.remove('active');
}

GENERATE_BTN.addEventListener('click', () => {
   const URL = `http://localhost:8080/generate?start=${startingPoint}&end=${endPoint}&stops=${Array.from(intermediatePoints)}&date=2022-01-21T17:00:00`;
   fetch(URL, {method: 'GET', headers: {'Content-Type': 'application/json'}})
       .then(resp => resp.json()).then(data => {
           visualRoutes(data);
   });
});

function visualRoutes(data) {
    document.getElementById('pickStartingPoint').remove();
    document.getElementById('pickEndPoint').remove();
    document.getElementById('addIntermediatePoint').remove();
    document.getElementById('generateBtn').remove();
    document.querySelectorAll('.deleteIntermediatePoint').forEach(p => p.remove());
    if(visualRoute(data[0], 'route1Visualization', 'Najszybsza trasa')) {
        const SHOW_ALTERNATIVE_ROUTE_BTN = document.createElement('div');
        SHOW_ALTERNATIVE_ROUTE_BTN.id = 'showAlternativeRouteBtn';
        SHOW_ALTERNATIVE_ROUTE_BTN.innerHTML = 'Pokaż więcej';
        document.body.appendChild(SHOW_ALTERNATIVE_ROUTE_BTN);
        SHOW_ALTERNATIVE_ROUTE_BTN.addEventListener('click', () => {
            visualRoute(data[1], 'route2Visualization', 'Trasa alternatywna');
            SHOW_ALTERNATIVE_ROUTE_BTN.remove();
        });
    }
}

function reloadPage(){
    location.href = `route-generator.html`;
}

function visualRoute(data, parent, label) {
    const routeVisualization = document.getElementById(parent);
    routeVisualization.classList.add('active');
    if(data == null) {
        routeVisualization.innerHTML = `<p><h1 style="margin-bottom: 1vh">Nie znaleziono trasy dla podanych kryteriów.</h1></p>
                                            <i id="refreshBtn" onclick="reloadPage()" class="fas fa-sync-alt"></i>`;
        return false;
    }
    routeVisualization.innerHTML = `<h1 style="margin-bottom: 1vh">${label}(${data['estimatedTravelTime']}min. | ${data['pointsToEarn']}pkt)</h1>`;
    data['trails'].forEach(trail => {
        let startingPointName = trail['startingPoint']['name'];
        let endPointName = trail['endPoint']['name'];
        let color = (trail['color'] == 'BZ') ? 'white':trail['color'];

        let trailVisualizationElem = document.createElement('div');
        trailVisualizationElem.classList.add('trailVisualization');
        trailVisualizationElem.innerHTML =
            `<p>${startingPointName} <span style="color: ${color}"> <span style="font-size: 40px; font-weight: bold;">----</span><span style="color: black; font-weight: bold;">${trail['pointsForReaching']}/${trail['pointsForDescent']}</span><span style="font-size: 40px; font-weight: bold;">----</span> </span> ${endPointName} <span style="font-weight: bold;">${trail['estimatedTime']}</span>min. <span style="font-weight: bold;">${trail['heightDifference']}</span>m.</p> `;
        routeVisualization.appendChild(trailVisualizationElem);
    });
    return true;
}

function updateMountainPointList(rangeSelectId, toUpdateSelectListId) {
    const URL = `http://localhost:8080/points?range=${document.getElementById(rangeSelectId).value}`;
    fetch(URL, {method: 'GET', headers: {'Content-Type': 'application/json'}})
        .then(resp => resp.json()).then(data => {
        if(data.length == 0) {
            if(rangeSelectId == 'startingPointMountainRange')
                SAVE_SELECTED_STARTING_POINT_BTN.classList.add('hidden');
            else if(rangeSelectId == 'endPointMountainRange')
                SAVE_SELECTED_END_POINT_BTN.classList.add('hidden');
            else if(rangeSelectId == 'intermediatePointMountainRange')
                SAVE_SELECTED_INTERMEDIATE_POINT_BTN.classList.add('hidden');
        }
        else {
            if(rangeSelectId == 'startingPointMountainRange')
                SAVE_SELECTED_STARTING_POINT_BTN.classList.remove('hidden');
            else if(rangeSelectId == 'endPointMountainRange')
                SAVE_SELECTED_END_POINT_BTN.classList.remove('hidden');
            else if(rangeSelectId == 'intermediatePointMountainRange')
                SAVE_SELECTED_INTERMEDIATE_POINT_BTN.classList.remove('hidden');
        }
            fillMountainPointsSelectList(data, document.getElementById(toUpdateSelectListId));
        });
}




