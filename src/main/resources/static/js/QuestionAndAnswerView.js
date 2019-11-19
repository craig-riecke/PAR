class QuestionAndAnswerView {
    constructor(qaModel) {
        this.qaModel = qaModel;
        this.questionView = new QuestionView(qaModel.questionText);
        this.answerView = new AnswerView(qaModel);
        this.element = buildQuestionAndAnswerViewElement(qaModel.id + "QuestionAndAnswerView", this.questionView.element, this.answerView.element);
    }

    getResponse(){
        if (this.answerView.getCurrentAnswer() === ResponseResult.blank){
            return null;
        }
        else {
            let response = {
                questionId: this.qaModel.id
            };
            let questionTextResponse = this.questionView.getResponse();
            if (questionTextResponse !== null) {
                response.questionText = questionTextResponse;
            }
            response.answerText = this.answerView.getCurrentAnswer();
            return response;
        }
    }

    checkAnswerAndUpdateView(){
        return this.answerView.checkAnswerAndUpdateView();
    }
}

function buildQuestionAndAnswerViewElement(id, questionView, answerView) {
    let element = document.createElement("div");
    element.setAttribute("id", id);
    element.appendChild(questionView);
    element.appendChild(answerView);
    element.classList.add("pad5");
    return element;
}
