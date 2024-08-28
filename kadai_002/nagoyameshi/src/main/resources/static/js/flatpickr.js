document.addEventListener('DOMContentLoaded', function() {
    // 現在の日付を取得し、3ヶ月先の日付を計算
    let maxDate = new Date();
    maxDate.setMonth(maxDate.getMonth() + 3);

    // Flatpickrの初期化
    flatpickr('#reservationDate', {
        locale: 'ja', // 日本語ロケールを使用
        minDate: 'today', // 予約可能な最小日付を今日に設定
        maxDate: maxDate // 予約可能な最大日付を3ヶ月先に設定
    });
});