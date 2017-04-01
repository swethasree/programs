/// <reference path="question.js" />
/// <reference path="quiz_controller.js" />
function populate() {
    if (quiz.isEnded()) {
    }
    else {
        var element = document.getElementById("question");
        element.innerHTML = quiz.getQuestionIndex().text;

        var choices = quiz.getQuestionIndex().choices;
        for (var i = 0; i < choices.length; i++) {

            var element = document.getElementById("choice" + i);
            element.innerHTML = choices[i];
        }
    }
}
var questions = [
    new Question("Which one is not an oops?", ["java", "C#", "C++", "c"], "c"),
    new Question("Which one is used for styling web pages?", ["HTML", "JQuery", "CSS", "xml"], "CSS"),
    new Question("There are_____  main components of oops", ["1", "6", "2", "4"], "4"),
    new Question("Which languag is used for web apps?", ["PHP", "Python", "Javascript", "All"], "All"),
    new Question("MVC is a_____", ["Language", "Library", "Framework", "All"], "Framework"),

    ];
    var quiz = new Quiz(questions);
    populate();