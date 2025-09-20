jQuery.fn.serializeObject = function() {
	let obj = null;
	try {
		if (this[0].tagName && this[0].tagName.toUpperCase() == "FORM") {
			let arr = this.serializeArray();
			if (arr) {
				obj = {};
				jQuery.each(arr, function() {
					obj[this.name] = this.value;
				});
			}
		}
	} catch (e) {
		alert(e.message);
	} finally {
	}

	return obj;
};

$(document).ajaxSend(function(event, request, settings) {
	request.setRequestHeader('AJAX', 'true');
});

$(document).ajaxError(function(event, request) {
	if( 401 == request.status ){
		//alert('로그인 유지 시간이 초과되어, 초기 화면으로 이동합니다.');
		//location.replace('/');
		openModal('modal100');
	}else{
		//alert('작업 중 오류가 발생 하였습니다. 잠시 후 다시 시도해 주시기 바랍니다.');
		if (request.responseJSON && request.responseJSON.message) {
			var errorMessage = request.responseJSON.message;
			if (errorMessage.includes("###")) {
				errorMessage = "작업 중 오류가 발생 하였습니다. 잠시 후 다시 시도해 주시기 바랍니다.";
			}
			$("#modalErrorMsg").html(errorMessage);
		}
		if( 200 != request.status ){
			openModal('modal999');
		}
	}
});

// 쿠키 저장
function setCookie(cookieName, value, exdays) {
	let exdate = new Date();
	exdate.setDate(exdate.getDate() + exdays);

	let cookieValue = escape(value) + "; path:/" + ((exdays == null) ? "" : "; expires=" + exdate.toGMTString());
	document.cookie = cookieName + "=" + cookieValue;
	document.setPath = "/";
}

// 쿠키 삭제
function deleteCookie(cookieName) {
	let expireDate = new Date();
	expireDate.setDate(expireDate.getDate() - 1);
	document.cookie = cookieName + "= " + "; expires=" + expireDate.toGMTString();
}

// 쿠키 조회
function getCookie(cookieName) {
	cookieName = cookieName + '=';
	let cookieData = document.cookie;
	let start = cookieData.indexOf(cookieName);
	let cookieValue = '';
	if (start != -1) {
		start += cookieName.length;
		let end = cookieData.indexOf(';', start);
		if (end == -1) end = cookieData.length;
		cookieValue = cookieData.substring(start, end);
	}
	return unescape(cookieValue);
}

//get 파라메터
function replaceQueryValue( href, key, value) {
	value = encodeURIComponent( value );
	let idx0 = href.indexOf( '?' );
	if( idx0 < 0 ) {
		if( value ) href = href +"?"+ key +"="+ value;
		return href;
	}

	let idx1 = href.indexOf( "?"+ key +"=", idx0 );
	if( idx1 < 0 ) idx1 = href.indexOf( "&"+ key +"=", idx0 );
	if( idx1 < 0 ) {
		if( value ) href = href +"&"+ key +"="+ value;
		return href;
	}

	let idx2 = href.indexOf( "&", idx1+1 );
	if( idx2 < 0 ) {
		if( value )
			href = href.substring( 0, ++idx1 ) + key +"="+ value;
		else
			href = href.substring( 0, idx1 );
	} else {
		if( value )
			href = href.substring( 0, ++idx1 ) + key +"="+ value + href.substring( idx2 );
		else
			href = href.substring( 0, idx1+1 ) + href.substring( idx2+1 );

		idx1 = href.indexOf( "&"+ key +"=", idx1 );
		while( idx1 >= 0 ) {
			idx2 = href.indexOf( "&", idx1+1 );
			if( idx2 < 0 )
				return href.substring( 0, idx1 );
			else {
				href = href.substring( 0, idx1 ) + href.substring( idx2 );
				idx1 = href.indexOf( "&"+ key +"=", idx1 );
			}
		}
	}

	return href;
}

//POST전송
function pageGoPost(data){
	var innerTag = "";

	for (var i = 0; i < data.values.length; i++) {
		innerTag += "<input type='hidden' name='"+ data.values[i][0] +"' value='"+ data.values[i][1] +"'>";
	}

	var frm = $("<form>", {
		method: "post",
		action: data.url,
		target: data.target,
		html: innerTag
	}).appendTo("body");

	frm.submit();
}

//비밀번호 체크
function checkPassword(pass) {
	var regExp = /^(?!((?:[A-Za-z]+)|(?:[~!@#$%^&*()_+=]+)|(?:[0-9]+))$)[A-Za-z\d~!@#$%^&*()_+=]{10,16}$/;
	if (regExp.test(pass) == false) {
		return false;
	}else{
		return true;
	}
}

//아이디 체크
function checkId(id) {
	var regExp = /^[a-z0-9]{4,16}$/;
	if (regExp.test(id) == false) {
		return false;
	}else{
		return true;
	}
}

//이메일 체크
function checkEmail(email) {
	var regExp = /^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/
	if (regExp.test(email) == false) {
		return false;
	}else{
		return true;
	}
}

// 사업자번호 형식 유효성 검사
function checkBizNo(bizNo) {
	let regExp = /^\d{3}-\d{2}-\d{5}$/;
	if (!regExp.test(bizNo)) {
		return false;
	}else{
		return true;
	}
}

//숫자 체크
function checkNumber(v) {
	var regExp = /^[0-9]+$/
	if (regExp.test(v) == false) {
		return false;
	}else{
		return true;
	}
}

//숫자 포맷
function replaceNumber(e) {
	e.value = e.value.replace(/[^0-9]/g, '');
}


//모달 열기
function openModal(v, focusTarget) {
	$('body').css('overflow', 'hidden');
	const modal = $("#"+v);
	modal.fadeIn();
	modal.focus();
	modal.one("keyup", function (event) {
		// 엔터키, ESC 키 눌렀을 때 모달 닫기
		if (event.keyCode == 13 || event.keyCode == 27 || event.which == 27) {
			closeModal(v, focusTarget);
		}
	});
}

//모달 닫기
function closeModal(v, focusTarget){
	$('body').css('overflow', 'unset');
	$("#"+v).fadeOut();
	if (focusTarget !== undefined) {
		$(focusTarget).focus();
	}
}

//로그인 Modal 열기
function openLoginModal() {

	$("#loginModal").fadeIn();
	// history.pushState({ modalOpen: true },'', window.location.href);
	//body 스크롤 방지
	document.body.classList.add("stop-scroll");
}

//로그인 Modal 열기
function closeLoginModal() {
	$(".non_member_buy").show();
	if(history?.state?.modalOpen){
		history.back();
	}
	$("#loginModal").fadeOut();
	//body 스크롤 생성
	document.body.classList.remove("stop-scroll");
}

//PC 모바일 구분
const isMobile = () => {
	return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

// 앱여부
const isApp = () => {
	let userAgent = navigator.userAgent;
	return userAgent.indexOf("KARYMARKET_APP") > -1;
}

// 고유 ID 생성
function generateUniqueId() {
	return Math.random().toString(36).substr(2, 9);
}

const getHost = () => {
	return window.location.protocol + "//" + window.location.host;
}

function addComma(num) {
	if(num > 0) {
		var regexp = /\B(?=(\d{3})+(?!\d))/g;
		return num.toString().replace(regexp, ',');
	}else {
		return num;
	}
}

Handlebars.registerHelper('cond', function (v1, operator, v2, options) {
	switch (operator) {
		case '==':
			return (v1 == v2) ? options.fn(this) : options.inverse(this);
		case '===':
			return (v1 === v2) ? options.fn(this) : options.inverse(this);
		case '<':
			return (v1 < v2) ? options.fn(this) : options.inverse(this);
		case '<=':
			return (v1 <= v2) ? options.fn(this) : options.inverse(this);
		case '>':
			return (v1 > v2) ? options.fn(this) : options.inverse(this);
		case '>=':
			return (v1 >= v2) ? options.fn(this) : options.inverse(this);
		case '&&':
			return (v1 && v2) ? options.fn(this) : options.inverse(this);
		case '||':
			return (v1 || v2) ? options.fn(this) : options.inverse(this);
		case '!=':
			return (v1 != v2) ? options.fn(this) : options.inverse(this);
		default:
			return options.inverse(this);
    	}
});

Handlebars.registerHelper('formatCurrency', function(value) {
    return value.toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,");
});

let isEmpty = function(value){
  if( value == "" || value == null || value == undefined || ( value != null && typeof value == "object" && !Object.keys(value).length ) ){
    return true
  }else{
    return false
  }
};

// < > & " ' = 를 HTML 엔티티로 변환
function escapeHtml(str) {
	if (isEmpty(str)) {
		return "";
	}
	return str.replace(/&/g, "&amp;")
		.replace(/</g, "&lt;")
		.replace(/>/g, "&gt;")
		.replace(/"/g, "&quot;")
		.replace(/'/g, "&#039;")
		.replace(/=/g, "&#x3d;");
}

// &amp; &lt; &gt; &quot; &#039; &#x3d; 를 < > & " ' 로 변환
function unescapeHtml(str) {
	if (isEmpty(str)) {
		return "";
	}
	return str.replace(/&amp;/g, "&")
		.replace(/&lt;/g, "<")
		.replace(/&gt;/g, ">")
		.replace(/&quot;/g, '"')
		.replace(/&#039;/g, "'")
		.replace(/&#x3d;/g, "=");
}

function addCommasToNumber(number) {
	return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 장바구니 카운트
function getCartCount() {
	$.ajax({
		url     : "/rest/cart/count",
		method  : "get",
		dataType: "json",
	})
	.done(function (res) {
		if (res.body > 0) {
			$("#head_cart_count").text(res.body);
			$("#head_cart_count").css("display", "flex");
			$("#fnb_cart_count").text(res.body);
			$("#fnb_cart_count").css("display", "flex");
		} else {
			$("#head_cart_count").text(0);
			$("#fnb_cart_count").text(0);
			$("#head_cart_count").css("display", "none");
			$("#fnb_cart_count").css("display", "none");
		}
	})
}

// 뒤로가기 이벤트
window.addEventListener('pageshow', function(event) {
	if (event.persisted) {
		// 이전 페이지에서 복원된 경우에만 실행
		// 원하는 동작을 수행
		//location.reload()
		getCartCount();
	}
});

// 문자열에서 숫자들 , 기준으로 배열로 가져오기
function extractNumbersFromString(str) {
	const numbers = str.match(/-?\d+\.\d+/g); // Regular expression to match floating-point numbers
	if (numbers && numbers.length >= 2) {
		return numbers.slice(0, 2).map(parseFloat);
	}
	return [];
}


// 쿼리스트링에서 값 가져오기
function getQueryString(key) {
	var _key = key + '=';
	var _url = window.location.search;
	var _value = _url.substr(_url.indexOf(_key) + _key.length);
	if (_value.indexOf('&') != -1) {
		_value = _value.substr(0, _value.indexOf('&'));
	}
	return _value;
}

/**
 * window.scrollTo() 메서드에 속도 적용
 * @param y Y위치 값으로 스크롤 이동
 * @param duration 해당 위치로 이동하는 총 시간
 */
const durationScrollTo = (y, duration = 1000) => {
	const stepY = (y - window.scrollY) / duration;
	const currentY = window.scrollY;
	const startTime = new Date().getTime();

	const scrollInterval = setInterval(() => {
		const now = new Date().getTime() - startTime;

		window.scrollTo({ top: currentY + (stepY * now) });

		if (duration <= now) {
			clearInterval( scrollInterval );
		}
	}, 1);
};

/**
 * reload
 * @param split url에서 뒤로 붙는 문자열을 제거하기 위한 구분자. 없으면 url 전체를 reload 한다.
 */
const reload = (split) => {
	url = split ? window.location.href.split(split)[0] : window.location.href;
	window.location.href = url;
};

function formatDate(date) {
	const year = date.getFullYear();
	let month = date.getMonth() + 1; // getMonth()는 0부터 시작하므로 1을 더해줍니다
	let day = date.getDate();

	// 월과 일이 한 자리 숫자일 경우 앞에 0을 붙여 두 자리로 만듭니다
	if (month < 10) {
		month = '0' + month;
	}
	if (day < 10) {
		day = '0' + day;
	}

	return `${year}-${month}-${day}`;
}

function getGeoLocaltion(){
	navigator.geolocation.getCurrentPosition((position) => {
		console.log("lat: " + position.coords.latitude)
		console.log("lng: " + position.coords.longitude)
	});
}
