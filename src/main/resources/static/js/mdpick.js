// 초기값 설정
document.querySelector('.off.pick1').style.display = 'none';
document.querySelector('.on.pick1').style.display = 'block';
document.querySelector('.off.pick2').style.display = 'block';
document.querySelector('.on.pick2').style.display = 'none';

// 클래스 item과 rel이 0인 경우의 초기값 설정
document.querySelector('.item[rel="0"]').style.display = 'block';

// 클래스 item과 rel이 1인 경우의 초기값 설정
document.querySelector('.item[rel="1"]').style.display = 'none';

// 주기적으로 실행할 함수
function toggleDisplay() {
    // 클래스 off.pick1과 on.pick1의 display 속성 전환
    document.querySelector('.off.pick1').style.display =
        (document.querySelector('.off.pick1').style.display === 'none') ? 'block' : 'none';
    document.querySelector('.on.pick1').style.display =
        (document.querySelector('.on.pick1').style.display === 'none') ? 'block' : 'none';

    // 클래스 off.pick2과 on.pick2의 display 속성 전환
    document.querySelector('.off.pick2').style.display =
        (document.querySelector('.off.pick2').style.display === 'none') ? 'block' : 'none';
    document.querySelector('.on.pick2').style.display =
        (document.querySelector('.on.pick2').style.display === 'none') ? 'block' : 'none';

    // 클래스 item과 rel이 0인 경우의 display 속성 전환
    document.querySelector('.item[rel="0"]').style.display =
        (document.querySelector('.item[rel="0"]').style.display === 'none') ? 'block' : 'none';

    // 클래스 item과 rel이 1인 경우의 display 속성 전환
    document.querySelector('.item[rel="1"]').style.display =
        (document.querySelector('.item[rel="1"]').style.display === 'none') ? 'block' : 'none';
}

// 일정한 시간 간격으로 함수 반복 실행 (여기서는 2000 밀리초, 즉 2초)
setInterval(toggleDisplay, 2000);







