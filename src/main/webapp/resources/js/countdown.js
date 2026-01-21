/**
 * 
 */function initCountdown(slotDate, slotStart, elementId) {
    if (!slotDate || !slotStart) {
        return;
    }

    if (slotStart.length > 5) {
        slotStart = slotStart.substring(0, 5);
    }

    var target = new Date(slotDate + "T" + slotStart);

    function updateCountdown() {
        var now = new Date();
        var diff = target - now;
        var el = document.getElementById(elementId);
        if (!el) {
            return;
        }

        if (diff <= 0) {
            el.innerHTML = "C'est l'heure du test. Cliquez sur Entrer pour passer le test.";
            return;
        }

        var seconds = Math.floor(diff / 1000) % 60;
        var minutes = Math.floor(diff / (1000 * 60)) % 60;
        var hours   = Math.floor(diff / (1000 * 60 * 60));

        el.innerHTML = "Le test commencera dans "
            + hours + " h "
            + minutes + " min "
            + seconds + " s.";
    }

    updateCountdown();
    setInterval(updateCountdown, 1000);
}
