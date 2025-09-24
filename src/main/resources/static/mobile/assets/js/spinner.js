var spinner;
var spinnerActive = false;

window.addEventListener("beforeunload", function() {
    spinnerStart();
    setTimeout(() => spinnerStop(), 3000);
});

window.addEventListener("load", function() {
    spinnerStop();
});

window.addEventListener("popstate", function() {
    spinnerStop();
});

function spinnerStart() {
    if (!spinnerActive) {
        // let createLayDiv = document.createElement("div");
        // createLayDiv.setAttribute("id", "spinnerLay1000");
        // document.body.appendChild(createLayDiv);
        //
        // let createSpinDiv = document.createElement("div");
        // createSpinDiv.setAttribute("id", "spinnerContainer1000");
        // document.getElementById("spinnerLay1000").appendChild(createSpinDiv);
        //
        // // 로딩 이미지 생성
        // var loadingImage = document.createElement('img');
        // loadingImage.src = '/images/loading.gif'; // 로딩 이미지 경로를 적어주세요
        // loadingImage.alt = 'Loading...';
        // loadingImage.style.width = '230px';

        // [스핀 옵션 지정 실시]
        // let opts = {
        //     lines: 10, // 그릴 선의 수 [20=원형 / 10=막대] [The number of lines to draw]
        //     length: 10, // 각 줄의 길이 [0=원형 / 10=막대] [The length of each line]
        //     width: 15,  // 선 두께 [The line thickness]
        //     radius: 42, // 내부 원의 반지름 [The radius of the inner circle]
        //     // scale: 0.85, // 스피너의 전체 크기 지정 [Scales overall size of the spinner]
        //     scale: 0.55, // 스피너의 전체 크기 지정 [Scales overall size of the spinner]
        //     corners: 1, // 모서리 라운드 [Corner roundness (0..1)]
        //     // color: '#003399', // 로딩 CSS 색상 [CSS color or array of colors]
        //     color: '#ffffff', // 로딩 CSS 색상 [CSS color or array of colors]
        //     fadeColor: 'transparent', // 로딩 CSS 색상 [CSS color or array of colors]
        //     opacity: 0.05, // 선 불투명도 [Opacity of the lines]
        //     rotate: 0, // 회전 오프셋 각도 [The rotation offset]
        //     direction: 1, // 회전 방향 시계 방향, 반시계 방향 [1: clockwise, -1: counterclockwise]
        //     speed: 1, // 회전 속도 [Rounds per second]
        //     trail: 74, // 꼬리 잔광 비율 [Afterglow percentage]
        //     fps: 20, // 초당 프레임 수 [Frames per second when using setTimeout() as a fallback in IE 9]
        //     zIndex: 2e9 // 인덱스 설정 [The z-index (defaults to 2000000000)]
        // };


        // let target = document.getElementById("spinnerContainer1000");
        // target.appendChild(loadingImage);
        // // spinner = new Spinner(opts).spin(target);

        document.getElementById("spinnerLay1000").style.display = "block";
        spinnerActive = true;
    }
}

function spinnerStop() {
    if (spinnerActive) {
        if (spinner) {
            spinner.stop();
        }
        try {
            let tagId = document.getElementById("spinnerLay1000");
            if (tagId) {
                document.getElementById("spinnerLay1000").style.display = "none";
            }
        } catch (exception) {
            console.error("catch : " + exception);
        }
        spinnerActive = false;
    }
}
