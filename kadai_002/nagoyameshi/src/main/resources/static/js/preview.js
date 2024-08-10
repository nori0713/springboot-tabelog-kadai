const imageInput = document.getElementById('imageFile');
const imagePreview = document.getElementById('imagePreview');

imageInput.addEventListener('change', () => {
    if (imageInput.files && imageInput.files[0]) {
        let fileReader = new FileReader();
        fileReader.onload = () => {
            imagePreview.innerHTML = `<img src="${fileReader.result}" class="img-fluid mb-3" alt="プレビュー画像">`;
        }
        fileReader.readAsDataURL(imageInput.files[0]);
    } else {
        imagePreview.innerHTML = '';
    }
});