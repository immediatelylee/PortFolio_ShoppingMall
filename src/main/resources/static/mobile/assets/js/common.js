// $(".hamburger_menu_depth1").click(function() {
//     $(this).next(".hamburger_menu_depth2").stop().slideToggle(300);
//     $(this).toggleClass('on').siblings().removeClass('on');
//     $(this).next(".hamburger_menu_depth2").siblings(".hamburger_menu_depth2").slideUp(300); // 1개씩 펼치기
// });


// $(".delivery_cont_depth1").click(function() {
//     $(this).next(".delivery_cont_depth2").stop().slideToggle(300);
//     $(this).toggleClass('on').siblings().removeClass('on');
//     $(this).next(".delivery_cont_depth2").siblings(".delivery_cont_depth2").slideUp(300); // 1개씩 펼치기
// });

//
// $('.inner_tab_menu_list').click(function(){
//     $('.inner_tab_menu_list').removeClass('on');
//     $(this).addClass('on');
//     $(this).next('.inner_tab_menu_list').addClass('on');
// });


// tab bar 스크롤시 보였다 없애기
$(function () {

    // let prevScrollpos = $(window).scrollTop();
    // $(window).scroll(function () {
    //     let currentScrollPos = $(window).scrollTop();
    //     if (prevScrollpos > currentScrollPos) {
    //         $(".tab_bar").css("bottom", "0");
    //     } else {
    //         // $(".tab_bar").css("bottom", "-60px");
    //         $(".tab_bar").css("bottom", "-7.5rem");
    //     }
    //     prevScrollpos = currentScrollPos;
    // });

    let prevScrollpos = $(window).scrollTop();
    // $(window).scroll(function () {
    //     let currentScrollPos = $(window).scrollTop();
    //     if (prevScrollpos > currentScrollPos) {
    //         $(".scroll_top").addClass("show"); // 버튼을 보이게 함
    //         $(".tab_bar").css("bottom", "0");
    //     } else {
    //         // $(".tab_bar").css("bottom", "-60px");
    //         $(".scroll_top").removeClass("show"); // 스크롤 위치가 400px 미만이면 버튼을 숨김
    //         $(".tab_bar").css("bottom", "-7.5rem");
    //     }
    //     prevScrollpos = currentScrollPos;
    // });
    $(window).scroll(function () {
        let currentScrollPos = $(window).scrollTop();

        // 2024-11-05 삭제 (스크롤 시 탭바 보이고 숨기기 제거)
        // if (prevScrollpos > currentScrollPos) {
        //     // Scrolling up
        //     $(".tab_bar").css("bottom", "0");
        //     // $(".scroll_top").addClass("show");
        // } else {
        //     // Scrolling down
        //     $(".tab_bar").css("bottom", "-7.5rem");
        //     // $(".scroll_top").removeClass("show");
        // }

        // Check if the scroll position is less than 400 pixels
        if (currentScrollPos < 400) {
            // $(".tab_bar, .scroll_top").removeClass("show");
            $(".tab_bar").removeClass("show");
        }

        prevScrollpos = currentScrollPos;
    });

    // Initially hide the ".scroll_top" element
    // $(".scroll_top").removeClass("show");

    // 버튼을 클릭하면 페이지 상단으로 스크롤
    $('#top_btn').click(function() {
        $('html, body').animate({scrollTop: 0}, 800); // 800ms 동안 페이지 상단으로 스크롤
        return false;
    });

});


$(".store_cont_depth1").click(function() {
    $(this).next(".store_cont_depth2").stop().slideToggle(300);
    $(this).toggleClass('on').siblings().removeClass('on');
    $(this).next(".store_cont_depth2").siblings(".store_cont_depth2").slideUp(300); // 1개씩 펼치기
});


// 모달창
$(function(){

    $(".profile_icon").click(function(){
        $(".modal").fadeIn();
    });

    $(".modal_close").click(function(){
        $(".modal").fadeOut();
    });

});


// textarea 글 입력하면 라벨 안보이게하기
$("label + textarea").keyup(function () {
    if ($(this).val() != "") {
        $(this).prev().hide();
    } else {
        $(this).prev().show();
    }
}).prev().click(function () {
    $(this).next().focus();
});



// 비밀번호 입력창 눈아이콘 클릭하면 비밀번호 보이기
$(document).ready(function () {
    $('.icon_pw_eye').on('click', function () {
        $('input').toggleClass('show');
        if ($('input').hasClass('show')) {
            $(this).attr('class', "icon_pw_eye")
                .prev('input').attr('type', "text");
        } else {
            $(this).attr('class', "icon_pw_eye")
                .prev('input').attr('type', 'password');
        }
    });
});




class UrlSchemeCaller {
    windowState = 'focus';

    constructor() {
        this.init();
    }

    init() {
        window.addEventListener('focus', () => {
            this.windowState = 'focus';
        });

        window.addEventListener('blur', () => {
            this.windowState = 'blur';
        });
    }

    call(urlScheme, notInstalledCallback) {
        location.href = urlScheme;

        setTimeout(() => {
            if (this.windowState === 'focus') {
                // 앱이 설치되어 있지 않은 상태
                notInstalledCallback();
            }
        }, 300);
    }
}

window.urlSchemeCaller = new UrlSchemeCaller();


function userLocation(store, customerSeq){
    if (customerSeq == "KM_3225246723@k"
        || customerSeq == "MBS_3691661020@k"
        || customerSeq == "MBS_3691716910@k"
        || customerSeq == "KM_1731199674@k") {
        $("#left_FNB").show();
    } else {
        // 위치정보를 기반으로 FNB 접근 권한 체크
        navigator.geolocation.getCurrentPosition((position) => {
            $.ajax({
                url: "/rest/product/fnb/access/" + store + "/"
                    + position.coords.latitude + "/"
                    + position.coords.longitude,
                method: "get",
                dataType: "json",
            })
            .done(function (res) {
                if (res.status == 'S') {
                    if (res.body == "Y") {
                        $("#left_FNB").show();
                        console.log("Permitted")
                    }
                }
            });
        });
    }
}
