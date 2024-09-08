document.addEventListener('DOMContentLoaded', function() {
    // 予約日選択に関する設定
    let maxDate = new Date();
    maxDate.setMonth(maxDate.getMonth() + 3);  // 3ヶ月先の日付を設定

    let minDate = new Date();
    minDate.setDate(minDate.getDate() + 1); // 今日から1日加算して明日を最小日に設定

    // 予約日選択用のFlatpickr初期化
    const reservationDateInput = document.querySelector('#reservationDate');
    if (reservationDateInput) {
        flatpickr(reservationDateInput, {
            locale: 'ja',           // 日本語ロケール
            minDate: minDate,       // 明日を最小日付に設定
            maxDate: maxDate,       // 3ヶ月先を最大日付に設定
            dateFormat: "Y-m-d",    // 日付フォーマットを指定
            defaultDate: minDate    // デフォルトを明日に設定
        });
    }
});