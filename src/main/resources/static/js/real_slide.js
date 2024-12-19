var slides = document.querySelector('.slides'),
    slide = document.querySelectorAll('.slides li'),
    currentIdx = 0,
    slideCount = slide.length,
    slideWidth = 1920,
    slideMargin = 0,
    prevBtn = document.querySelector('.prev'),
    nextBtn = document.querySelector('.next'),
    // 추가본
    toggleBtn = document.querySelector('.toggle'),
    isPaused = false, // Variable to track if the slideshow is paused
    // 페이지네이션
    SlideText = document.querySelector(".slideText");

console.log(SlideText)
makeClone();
function makeClone(){
    for(var i = 0; i<slideCount; i++){
        //a.cloneNode() , a.cloneNode(true)
        var cloneSlide = slide[i].cloneNode(true);
        cloneSlide.classList.add('clone');
        // a.appendChild(b)
        slides.appendChild(cloneSlide);
    }
    for(var i = slideCount -1;i>=0;i--){
        //a.cloneNode() , a.cloneNode(true)
        var cloneSlide = slide[i].cloneNode(true);
        cloneSlide.classList.add('clone');
        // a.appendChild(b)
        slides.prepend(cloneSlide);
    }
    updateWidth();
    setInitialPos();

    setTimeout(function(){
        slides.classList.add('animated');
    },100);
}

function updateWidth(){
    var currentSlides = document.querySelectorAll('.slides li');
    var newSlideCount = currentSlides.length;

    var newWidth = (slideWidth + slideMargin)*newSlideCount - slideMargin +'px';
    slides.style.width = newWidth;

}
function setInitialPos(){
    var initialTranslateValue = -(slideWidth + slideMargin)*slideCount
    // slides {transform:translateX(-1000px);}
    slides.style.transform = 'translateX(' + initialTranslateValue+'px)';
}

nextBtn.addEventListener('click',function(){
    moveSlide(currentIdx +1);
    updateSlideText()
});

prevBtn.addEventListener('click',function(){
    moveSlide(currentIdx -1);
    updateSlideText()
});

function moveSlide(num){
    slides.style.left = -num * (slideWidth + slideMargin) +'px';
    currentIdx = num;
    // console.log(currentIdx,slideCount);
    if(currentIdx == slideCount || currentIdx == -slideCount){

        setTimeout(function(){
            slides.classList.remove('animated');
            slides.style.left = '0px';
            currentIdx = 0;
        },500);
        setTimeout(function(){
            slides.classList.add('animated');
        },600);
        updateSlideText()
    }
    updateSlideText()

}

var timer = undefined;
function autoSlide(){
    if (timer == undefined){
        timer = setInterval(function(){
            moveSlide(currentIdx + 1);
        }, 3000);
    }
}

autoSlide();

function stopSlide(){
    clearInterval(timer);
    timer = undefined;
}

toggleBtn.addEventListener('click', toggleSlideshow);
// 추가본
// function toggleSlideshow() {
//     if (isPaused) {
//         isPaused = false; // If paused, resume the slideshow
//         toggleBtn.textContent = '||'; // Change to play symbol
//         autoSlide(); // Start automatic slideshow
//     } else {
//         isPaused = true; // If not paused, pause the slideshow
//         toggleBtn.textContent = '▶'; // Change to pause symbol
//         stopSlide(); // Stop the slideshow
//     }
// }
function toggleSlideshow() {
    const pauseIcon = document.querySelector('.pause-icon');
    const playIcon = document.querySelector('.play-icon');

    if (isPaused) {
        isPaused = false; // Resume the slideshow
        pauseIcon.style.display = 'block'; // Show pause icon
        playIcon.style.display = 'none';  // Hide play icon
        autoSlide();
    } else {
        isPaused = true; // Pause the slideshow
        pauseIcon.style.display = 'none'; // Hide pause icon
        playIcon.style.display = 'block'; // Show play icon
        stopSlide();
    }
}


function updateSlideText(){
    SlideText.textContent = (currentIdx+1) + " / " + slideCount
    if (currentIdx+1 > slideCount){ SlideText.textContent = 1 +  " / " + slideCount};
}

updateSlideText()