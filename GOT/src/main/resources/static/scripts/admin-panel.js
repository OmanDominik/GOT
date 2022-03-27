const LOGOUT_OPTION = document.querySelector('nav:nth-of-type(2) .option:nth-child(1)');
LOGOUT_OPTION.addEventListener('click', () => {
    location.href = '/logout';
});


const MOUNTAIN_RANGE_FILTER_SELECT = document.getElementById('mountainRange');
const MOUNTAIN_RANGE_ADD_TRAIL_SELECT = document.getElementById('mountainRangeToAdd');

window.onload = function() {
    //const TRAILS_URL = 'http://localhost:8080/trails/filtered?search=&start=-&end=-&range=Gorce&color=-&minPoints=&maxPoints=&minTime=&maxTime=';
    const TRAILS_URL = 'http://localhost:8080/trails';
    fetch(TRAILS_URL, {method: 'GET', headers: {'Content-Type': 'application/json'}})
        .then(resp => {return resp.json()}).then(data => {generateList(data)}).then(() => turnOffLoadingAnimation());

    const RANGES_URL = 'http://localhost:8080/ranges';
    fetch(RANGES_URL, {method: 'GET', headers: {'Content-Type': 'application/json'}})
        .then(resp => {return resp.json()}).then(data => {
            fillMountainRangesSelectList(data, MOUNTAIN_RANGE_FILTER_SELECT);
            fillMountainRangesSelectList(data, MOUNTAIN_RANGE_ADD_TRAIL_SELECT);
        });

    const MOUNTAIN_POINTS_URL = 'http://localhost:8080/points';
    fetch(MOUNTAIN_POINTS_URL, {method: 'GET', headers: {'Content-Type': 'application/json'}})
        .then(resp => {return resp.json()}).then(data => {
        fillMountainPointsSelectList(data, document.querySelector('#startingPointsList'), true);
        fillMountainPointsSelectList(data, document.querySelector('#endPointsList'), true);
        fillMountainPointsSelectList(data, document.querySelector('#startingPointsToAddList'), false);
        fillMountainPointsSelectList(data, document.querySelector('#endPointsToAddList'), false);
    })
};

function generateList(data) {
    const trailsNotFound = document.getElementById('trailsNotFound');
    if(trailsNotFound != null)
        trailsNotFound.remove();

    document.querySelector('table').innerHTML = `<tr>
            <th style="width: 6%">ID</th>
            <th style="width: 33%">Punkt początkowy</th>
            <th style="width: 33%">Punkt końcowy</th>
            <th style="width: 7%">Kolor</th>
            <th style="width: 7%">Czas</th>
            <th style="width: 2%">Dwukierunkowy</th>
            <th style="width: 4%">Punkty</th>
            <th style="width: 8%">Akcja</th>
        </tr>`;
    for (let i = 0; i < data.length; i++) {
        displayTrail(data[i]);
    }
}

let trailIdToModification;
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

    document.querySelector('table').innerHTML += `<tr> <td>${trail["id"]}</td><td>${trail["startingPoint"]["name"]}</td>
                                         <td>${trail["endPoint"]["name"]}</td> <td><span style="background-color: ${trail["color"]}" class="trailColorCircle">${colorLabel}</span></td>
                                         <td>${trail["estimatedTime"]}min</td><td style="text-align: center; color: ${isOneWayColor}">${isOneWayIcon}</td>
                                         <td>${pointsLabel}</td><td id="actions${trail["id"]}" style="display: flex; justify-content: space-around">
                                         <div class="crud">
                                            <i class="fas fa-minus-circle" style="color: red"></i>
                                         </div>
                                         </td>
                                         </tr>`;

    let modifyActionElem = document.createElement('div');
    modifyActionElem.id = `modifyBtn${trail['id']}`;
    modifyActionElem.classList.add('crud');
    let i = document.createElement('i');
    i.classList.add('fas', 'fa-pencil-alt')
    i.style.color = '#90653e';
    modifyActionElem.appendChild(i);
    document.getElementById(`actions${trail['id']}`).appendChild(modifyActionElem);

    $(document).on('click', `#modifyBtn${trail['id']}`, function () {
        trailIdToModification = trail['id'];
        document.getElementById('modifyTrailPopup').classList.add('active');
        document.getElementById('overlay').classList.add('active');

        document.getElementById('startingPointModifyList').innerHTML = `<option selected>${trail["startingPoint"]["name"]}</option>`;
        document.getElementById('endPointModifyList').innerHTML = `<option selected>${trail["endPoint"]["name"]}</option>`;
        document.getElementById('mountainRangeModify').innerHTML = `<option selected>${trail["mountainRange"]}</option>`;
        document.getElementById('colorToModify').innerHTML =
            `<option value="RED" ${trail['color'] == 'RED' ? 'selected' : ''}>Czerwony</option>
                <option value="BLUE" ${trail['color'] == 'BLUE' ? 'selected' : ''}>Niebieski</option>
                <option value="GREEN" ${trail['color'] == 'GREEN' ? 'selected' : ''}>Zielony</option>
                <option value="YELLOW" ${trail['color'] == 'YELLOW' ? 'selected' : ''}>Żółty</option>
                <option value="BLACK" ${trail['color'] == 'BLACK' ? 'selected' : ''}>Czarny</option>
                <option value="BZ" ${trail['color'] == 'BZ' ? 'selected' : ''}>BZ</option>`;
        document.getElementById('pointsForReachingModify').value = `${trail["pointsForReaching"]}`;
        document.getElementById('pointsForDescentModify').value = `${trail["pointsForDescent"]}`;
        document.getElementById('estimatedTimeModify').value = `${trail["estimatedTime"]}`;
        document.getElementById('oneWayModify').checked = trail["oneWay"];
        if(trail['oneWay'])
            document.getElementById('pointsForDescentModifyDiv').style.display = 'none';

        document.getElementById('modifyBtn').addEventListener('click', () => {
            if(validateModifiedData(trail)) {
                document.getElementById('modifyTrailPopup').classList.remove('active');
                document.getElementById('overlay').classList.remove('active');
                const URL = `http://localhost:8080/trails/${trailIdToModification}`;
                fetch(URL, {method: 'PUT', headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({
                        color: document.getElementById('colorToModify').value,
                        oneWay: document.getElementById('oneWayModify').checked,
                        estimatedTime: document.getElementById('estimatedTimeModify').value,
                        pointsForReaching: document.getElementById('pointsForReachingModify').value,
                        pointsForDescent: document.getElementById('pointsForDescentModify').value,
                    }) }).then(resp => resp.json()).then(data => {
                    searchResult(trailIdToModification);
                });
            }
        });
    });
}

function validateModifiedData() {
    let pointsForReaching = document.getElementById('pointsForReachingModify').value;
    let pointsForDescent = document.getElementById('pointsForDescentModify').value;
    let estimatedTimeModify = document.getElementById('estimatedTimeModify').value;
    let oneWay = document.getElementById('oneWayModify').checked;
    let isValid = true;
    if(pointsForReaching <= 0 || pointsForReaching > 50) {
        document.getElementById('pointsForReachingModify').style.backgroundColor = '#da6e6e';
        isValid = false;
    }
    else {
        document.getElementById('pointsForReachingModify').style.backgroundColor = '';
    }
    if((pointsForDescent <= 0 || pointsForDescent > 50) && oneWay == false) {
        document.getElementById('pointsForDescentModify').style.backgroundColor = '#da6e6e';
        isValid = false;
    }
    else {
        document.getElementById('pointsForDescentModify').style.backgroundColor = '';
    }
    if(estimatedTimeModify <= 0 || estimatedTimeModify > 6000) {
        document.getElementById('estimatedTimeModify').style.backgroundColor = '#da6e6e';
        isValid = false;
    }
    else {
        document.getElementById('estimatedTimeModify').style.backgroundColor = '';
    }


    return isValid;
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
    const mountainRangeValue = MOUNTAIN_RANGE_FILTER_SELECT.value;
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

function searchResult(id) {
    const URL = `http://localhost:8080/trails/${id}`;
    document.querySelector('table').innerHTML = '';
    turnOnLoadingAnimation();
    fetch(URL, {method: 'GET', headers: {'Content-Type': 'application/json'}})
        .then(resp => {return resp.json()}).then(data => {
            let dummyList = [];
            dummyList[0] = data;
            generateList(dummyList);
    }).then(() => turnOffLoadingAnimation());
}


const FILTER_BTN = document.getElementById('filterBtn');
FILTER_BTN.addEventListener('click', displayFiltersMenu);
function displayFiltersMenu() {
    document.getElementById('filters').classList.toggle('active');
    document.getElementById('addingTrail').classList.remove('active');
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

const ADD_TRAIL_OPTION = document.getElementById('addTrailOption');
ADD_TRAIL_OPTION.addEventListener('click', displayAddTrailMenu);
function displayAddTrailMenu() {
    document.getElementById('filters').classList.remove('active');
    document.getElementById('addingTrail').classList.toggle('active');
}

function addEventListenersToPickPointBtnsToAdd() {
    document.querySelectorAll('.pickMountainPointToAdd').forEach(btn => {
        btn.addEventListener('click', () => {
            document.getElementById('overlay').classList.add('active');
            document.getElementById('pickMountainPointToAddPopUp').classList.add('active');
        });

    });
}
addEventListenersToPickPointBtnsToAdd();
let chosenStartingPointToAdd = '';
let chosenEndPointToAdd = '';
const SAVE_CHOSEN_POINTS_TO_ADD_BTN = document.getElementById('saveChosenPointsToAddBtn');
SAVE_CHOSEN_POINTS_TO_ADD_BTN.addEventListener('click', () => {
    chosenStartingPointToAdd = document.getElementById('startingPointsToAddList').value;
    chosenEndPointToAdd = document.getElementById('endPointsToAddList').value;

    document.getElementById('choosingStartingPointToAddParagraph').innerHTML =
        `<button class="pickMountainPointToAdd">Wybierz</button> Punkt początkowy: ${chosenStartingPointToAdd} <span id="deleteChosenStartingPointIconToAdd" class="icon"><i class="fas fa-times"></i></span>`;
    const deleteChosenStartingPointIconToAdd = document.getElementById('deleteChosenStartingPointIconToAdd');
    deleteChosenStartingPointIconToAdd.addEventListener('click', () => {
        document.getElementById('choosingStartingPointToAddParagraph').innerHTML =
            `<button class="pickMountainPointToAdd">Wybierz</button> Punkt początkowy:`;
        chosenStartingPointToAdd = '';
        document.getElementById('startingPointsToAddList').value = '';
        addEventListenersToPickPointBtnsToAdd();
    });

    document.getElementById('choosingEndPointToAddParagraph').innerHTML =
        `<button class="pickMountainPointToAdd">Wybierz</button> Punkt końcowy: ${chosenEndPointToAdd} <span id="deleteChosenEndPointIconToAdd" class="icon"><i class="fas fa-times"></i></span>`;
    const deleteChosenEndPointIconToAdd = document.getElementById('deleteChosenEndPointIconToAdd');
    deleteChosenEndPointIconToAdd.addEventListener('click', () => {
        document.getElementById('choosingEndPointToAddParagraph').innerHTML =
            `<button class="pickMountainPointToAdd">Wybierz</button> Punkt końcowy:`;
        chosenEndPointToAdd = '';
        document.getElementById('endPointsToAddList').value = '';
        addEventListenersToPickPointBtnsToAdd();
    });
    addEventListenersToPickPointBtnsToAdd();

    turnOffToAdPopup();
});

function turnOffToAdPopup() {
    document.getElementById('overlay').classList.remove('active');
    document.getElementById('pickMountainPointToAddPopUp').classList.remove('active');
}

document.getElementById('addTrailBtn').addEventListener('click', addTrail);

function updateAddTrailView() {
    if(document.getElementById('oneWay').checked)
        document.getElementById('pointsForDescentParagraph').style.display = 'none';
    else
        document.getElementById('pointsForDescentParagraph').style.display = 'block';
}

function addTrail() {
    let selectedColor = document.getElementById('colorToAdd').value;
    let oneWay = document.getElementById('oneWay').checked;
    let selectedRange = document.getElementById('mountainRangeToAdd').value;
    let pointsForReaching = document.getElementById('pointsForReaching').value;
    let pointsForDescent = oneWay ? '' : (document.getElementById('pointsForDescent').value);
    let estimatedTime = document.getElementById('estimatedTime').value;

    if(validateTrail(chosenStartingPointToAdd, chosenEndPointToAdd)) {
        const TRAILS_URL = `http://localhost:8080/trails?start=${chosenStartingPointToAdd}&end=${chosenEndPointToAdd}&range=${selectedRange}&color=${selectedColor}&pointsReaching=${pointsForReaching}&pointsDescent=${pointsForDescent}&time=${estimatedTime}&oneWay=${oneWay}`;
        fetch(TRAILS_URL, {method: 'POST', headers: {'Content-Type': 'application/json'}})
            .then(resp => {return resp.json()}).then(data => searchResult(data['id'])).then(() => visualCreatedTrail());
    }

}

function visualCreatedTrail() {
    const msg = `Pomyślnie stworzono szlak.`;
    alert(msg);
}

function validateTrail(startingPoint, endPoint) {
    let isValid = true;
    let pointsForReaching = document.getElementById('pointsForReaching').value;
    let pointsForDescent = document.getElementById('pointsForDescent').value;
    let estimatedTime = document.getElementById('estimatedTime').value;
    let oneWay = document.getElementById('oneWay').checked;
    if(pointsForReaching <= 0 || pointsForReaching > 50) {
        document.getElementById('pointsForReaching').style.backgroundColor = '#da6e6e';
        isValid = false;
    }
    else {
        document.getElementById('pointsForReaching').style.backgroundColor = '';
    }
    if((pointsForDescent <= 0 || pointsForDescent > 50) && oneWay == false) {
        document.getElementById('pointsForDescent').style.backgroundColor = '#da6e6e';
        isValid = false;
    }
    else {
        document.getElementById('pointsForDescent').style.backgroundColor = '';
    }
    if(estimatedTime <= 0 || estimatedTime > 6000) {
        document.getElementById('estimatedTime').style.backgroundColor = '#da6e6e';
        isValid = false;
    }
    else {
        document.getElementById('estimatedTime').style.backgroundColor = '';
    }

    if(startingPoint == '') {
        document.getElementById('choosingStartingPointToAddParagraph').style.backgroundColor = '#da6e6e';
        isValid = false;
    } else {
        document.getElementById('choosingStartingPointToAddParagraph').style.backgroundColor = '';
    }
    if(endPoint == '') {
        document.getElementById('choosingEndPointToAddParagraph').style.backgroundColor = '#da6e6e';
        isValid = false;
    } else {
        document.getElementById('choosingEndPointToAddParagraph').style.backgroundColor = '';
    }
    if(startingPoint == endPoint) {
        document.getElementById('choosingStartingPointToAddParagraph').style.backgroundColor = '#da6e6e';
        document.getElementById('choosingEndPointToAddParagraph').style.backgroundColor = '#da6e6e';
        isValid = false;
    } else if(startingPoint != '' && endPoint != '') {
        document.getElementById('choosingStartingPointToAddParagraph').style.backgroundColor = '';
        document.getElementById('choosingEndPointToAddParagraph').style.backgroundColor = '';
    }
    return isValid;
}

document.getElementById('closeModifyPopupIcon').addEventListener('click', () => {
    document.getElementById('modifyTrailPopup').classList.remove('active');
    document.getElementById('overlay').classList.remove('active');
});

function updateModifyView() {
    if(document.getElementById('oneWayModify').checked)
        document.getElementById('pointsForDescentModifyDiv').style.display = 'none';
    else
        document.getElementById('pointsForDescentModifyDiv').style.display = 'block';
}

document.getElementById('closePopupIconToAdd').addEventListener('click', () => {
    document.getElementById('pickMountainPointToAddPopUp').classList.remove('active');
    document.getElementById('overlay').classList.remove('active');

});
