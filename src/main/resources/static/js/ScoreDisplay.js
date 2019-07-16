class ScoreDisplay {
    constructor(scoreObject) {
        this.scoreObject = buildScore(scoreObject);
    }

    buildScore(scoreObject) {
        setCurrentScore(scoreObject);
    }
}

function setCurrentScore(scoreObject) {
    let scoreString = "";
    let scoreType = "ByType";
    if (scoreType === "ByType") {
        //scoreString=generateScoreByType(scoreObject);
        scoreString = generateScoreStringByType(scoreObject);
    } else if (scoreType === "SingleScore") {
        scoreString = generateSinglescore(scoreObject);
    } else if (scoreType === "Level") {
        scoreString = generateScoreLevel(scoreObject);
    }
    return scoreString;
}

function generateScoreByType(scoreObject) {
    //80-100 green
    //79-50 orange
    //49-0 red
    var breakdownString = "";
    for (var key in scoreObject) {
        if (scoreObject.hasOwnProperty(key)) {
            let value = scoreObject[key];
            if (value >= 80) {
                breakdownString += "&nbsp <i class=black>" + key + ":</i> <i class=green>" + value + "<i>";
            } else if (value <= 79 && value >= 50) {
                breakdownString += "<i class=black>" + key + ":</i> <i class=orange>" + value + "<i>";
            } else if (value <= 49) {
                breakdownString += "<i class=black>" + key + ":</i> <i class=red>" + value + "<i>";
            }
        }
        breakdownString += "<br />";
    }
    return breakdownString;

}

function generateScoreStringByType(scoreObject) {
    var visString = "";
    for (var key in scoreObject) { //key search
        if (scoreObject.hasOwnProperty(key)) {
            let value = scoreObject[key];
            visString += "&nbsp <i class=black>" + key + ": </i>";
            for (let i = 0; i < value.length; i++) { //value search on string
                if (value[i] == "O") {
                    visString += '<i class="fas fa-check-circle green"></i>';
                } else if (value[i] == "X") {
                    visString += '<i class="fas fa-times-circle red"></i>';
                } else if (value[i] == "_") {
                    visString += '<i class="fas fa-circle"></i>';
                } else {
                    visString += '<i class="fas fa-minus-circle yellow"></i>';
                }
            }
            visString += '<br />';
        }
    }
    return visString;
}
