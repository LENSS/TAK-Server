/**
 * Created by nedislav on 6/28/17.
 */
"use strict";

/*
 * Hide / Show more details of ERROR
 * fired on windows load
 * */
window.onload=function() {
    // console.log("document loaded");

    //cache DOM elements
    var showBtn = document.getElementById("show");
    var hideBtn = document.getElementById("hide");
    var errors = document.getElementsByClassName("error");
    // console.log(showBtn);
    // console.log(hideBtn);
    // console.log(errors);

    /*
     * hide function
     * */
    var hide = function () {
        showBtn.style.display = "inline-block";
        showBtn.style.marginTop = "10px";
        hideBtn.style.display = "none";
        for(var i = 0; i < errors.length; i++) {
            errors[i].style.display = "none";
        }
    };

    /*
     * show function
     * */
    var show = function () {
        showBtn.style.display = "none";
        hideBtn.style.display = "inline-block";
        hideBtn.style.margin = "10px";
        for(var i = 0; i < errors.length; i++) {
            errors[i].style.display = "block";
        }
    };

    /*
     * showBtn event attachment
     * */
    showBtn.addEventListener("click", show);

    /*
     * hideBtn event attachment
     * */
    hideBtn.addEventListener("click", hide);

    // default hiding of errors, JS only in case the client can't run javascript
    hide();
};
