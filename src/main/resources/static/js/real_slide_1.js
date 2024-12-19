var slides = document.querySelectorAll('.slides li');
var currentIdx = 0;
var slideCount = slides.length;
var prevBtn = document.querySelector('.prev');
var nextBtn = document.querySelector('.next');
var toggleBtn = document.querySelector('.toggle');
var isPaused = false;
var SlideText = document.querySelector(".slideText");

function updateAriaHidden() {
    slides.forEach((slide, index) => {
        if (index === currentIdx) {
            slide.setAttribute('aria-hidden', 'false');
            slide.style.display = 'block'; // 슬라이드를 보이게 설정
        } else {
            slide.setAttribute('aria-hidden', 'true');
            slide.style.display = 'none'; // 나머지 슬라이드를 숨김
        }
    });
}

function updateSlideText() {
    SlideText.textContent = (currentIdx + 1) + " / " + slideCount;
}

function moveSlide(newIdx) {
    if (newIdx < 0) {
        currentIdx = slideCount - 1; // 마지막 슬라이드로 이동
    } else if (newIdx >= slideCount) {
        currentIdx = 0; // 첫 번째 슬라이드로 이동
    } else {
        currentIdx = newIdx;
    }
    updateAriaHidden();
    updateSlideText();
}

nextBtn.addEventListener('click', function () {
    moveSlide(currentIdx + 1);
});

prevBtn.addEventListener('click', function () {
    moveSlide(currentIdx - 1);
});

var timer = undefined;

function autoSlide() {
    if (timer === undefined) {
        timer = setInterval(function () {
            moveSlide(currentIdx + 1);
        }, 3000);
    }
}

function stopSlide() {
    clearInterval(timer);
    timer = undefined;
}

toggleBtn.addEventListener('click', toggleSlideshow);

function toggleSlideshow() {
    const pauseIcon = document.querySelector('.pause-icon');
    const playIcon = document.querySelector('.play-icon');

    if (isPaused) {
        isPaused = false;
        pauseIcon.style.display = 'block';
        playIcon.style.display = 'none';
        autoSlide();
    } else {
        isPaused = true;
        pauseIcon.style.display = 'none';
        playIcon.style.display = 'block';
        stopSlide();
    }
}

// 초기화
updateAriaHidden();
updateSlideText();
autoSlide();