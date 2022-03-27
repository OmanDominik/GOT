function fillMountainRangesSelectListAndSetDeafultSettings(data, selectList) {
    for(let i = 0; i < data.length; i++) {
        if(data[i]['name'] == 'Tatry Zachodnie')
            selectList.innerHTML += `<option value="${data[i]['name']}" selected>${data[i]['name']}</option>`;
        else
            selectList.innerHTML += `<option value="${data[i]['name']}">${data[i]['name']}</option>`;
    }
}

function fillMountainRangesSelectList(data, selectList) {
    for(let i = 0; i < data.length; i++) {
        selectList.innerHTML += `<option value="${data[i]['name']}">${data[i]['name']}</option>`;
    }
}

function fillMountainPointsSelectList(data, selectList, includeBar) {
    if(includeBar)
        selectList.innerHTML = `<option selected value="-">-</option>`;
    else
        selectList.innerHTML = ``;

    for(let i = 0; i < data.length; i++) {
        const mountainPoint = data[i]['name'];
        selectList.innerHTML += `<option value="${mountainPoint}">${mountainPoint}</option>`;
    }
}

function displayMountainPointsPopUp() {
    document.getElementById('overlay').classList.add('active');
    document.getElementById('pickMountainPointPopUp').classList.add('active');
}

document.getElementById('closePopupIcon').addEventListener('click', turnOffPopup);

function turnOffPopup() {
    document.getElementById('overlay').classList.remove('active');
    document.getElementById('pickMountainPointPopUp').classList.remove('active');
}

