$(document).ready(function() {

    // 파일 추가 시 파일명 표시 이벤트 바인딩 : 20251217 최상림 추가
    $('.file_group input[type="file"]').change(function() {
        changeFile(this);
    });

    // 파일 삭제 버튼 클릭 파일 리셋 이벤트 바인딩 : 20251217 최상림 추가
    $('.file_group .clear_file').click(function() {
        const inputId = $(this).siblings('input[type="file"]').attr('id');
        clearFile(inputId);
    });

});

/**
 * 파일 input 값 변경 시 파일명 표시해주는 함수 : 20251217 최상림 추가
 * @param {Element} _this : this (파일 input)
 */
function changeFile(_this) {
    const files = _this.files;
    let output = '';
    if(files.length > 0) {
        output += files[0].name;
        if(files.length > 1) {
            output += ' 외 ' + (files.length - 1);
        }
        $(_this).siblings('.clear_file').show();
        $(_this).next().text(output);
    }
    else {
        clearFile(_this.id);
    }
}

/**
 * 파일 input 값 삭제 및 파일명 초기화하는 함수 : 20251217 최상림 추가
 * @param {String} inputId : 파일 input ID
 */
function clearFile(inputId) {
    const fileInput = $('#'+inputId);
    fileInput.val('');
    fileInput.siblings('span').text('');
    fileInput.siblings('.clear_file').hide();
}

/**
 * 폼 리셋 시 자동으로 초기화 되지 않는 것들 처리하는 함수 : 20251217 최상림 추가
 * @param {Element} _this : this (form)
 */
function clearForm(_this) {
    const fileInputItems = $(_this).find('.file_group input[type="file"]');
    for(let i = 0; i < fileInputItems.length; i++) {
        clearFile(fileInputItems.eq(i)[0].id);
    }
}