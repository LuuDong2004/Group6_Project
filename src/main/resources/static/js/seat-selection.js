let selectedSeats = [];
const maxSeats = 8;

function initializeSeatSelection(scheduleId, userId) {
    this.scheduleId = scheduleId;
    this.userId = userId;
    this.reservedSeats = [];

    // Load seat status from server
    loadSeatStatus(scheduleId).then(() => {
        // Initialize seat map after loading status
        const seats = document.querySelectorAll('.seat');
        seats.forEach(seat => {
            const seatId = parseInt(seat.getAttribute('data-seat-id'));
            const status = getSeatStatus(seatId);
            
            if (status === 'RESERVED') {
                seat.classList.add('reserved');
                seat.classList.remove('available');
            } else if (status === 'PENDING') {
                seat.classList.add('pending');
                seat.classList.remove('available');
            }
        });
    });
}

async function loadSeatStatus(scheduleId) {
    try {
        const response = await fetch(`/api/seats/status?scheduleId=${scheduleId}`);
        if (!response.ok) {
            throw new Error('Failed to load seat status');
        }
        const data = await response.json();
        this.reservedSeats = data;
        console.log('Loaded seat status:', data); // Debug log
        return data;
    } catch (error) {
        console.error('Error loading seat status:', error);
        return [];
    }
}

function getSeatStatus(seatId) {
    const seat = this.reservedSeats.find(s => s.seatId === seatId);
    console.log('Getting status for seat', seatId, ':', seat); // Debug log
    return seat ? seat.status : 'AVAILABLE';
}

function renderSeats() {
    const seatContainer = document.getElementById('seat-container');
    if (!seatContainer) return;

    seatContainer.innerHTML = '';

    // Tạo 8 hàng ghế (A-H)
    for (let row = 0; row < 8; row++) {
        const rowElement = document.createElement('div');
        rowElement.className = 'seat-row';
        
        // Tạo 8 ghế cho mỗi hàng
        for (let col = 0; col < 8; col++) {
            const seatNumber = col + 1;
            const seatName = `${String.fromCharCode(65 + row)}${seatNumber}`;
            const seatId = row * 8 + seatNumber;
            
            const seatElement = document.createElement('div');
            seatElement.className = 'seat';
            seatElement.textContent = seatName;
            seatElement.dataset.seatId = seatId;
            seatElement.dataset.seatName = seatName;

            // Kiểm tra trạng thái ghế
            const seatStatus = getSeatStatus(seatId);
            
            if (seatStatus === 'RESERVED') {
                seatElement.classList.add('reserved');
                seatElement.classList.remove('available');
            } else if (seatStatus === 'PENDING') {
                seatElement.classList.add('pending');
                seatElement.classList.remove('available');
            } else {
                seatElement.classList.add('available');
                seatElement.addEventListener('click', () => this.toggleSeat(seatId, seatName, seatElement));
            }

            rowElement.appendChild(seatElement);
        }
        
        seatContainer.appendChild(rowElement);
    }
}

function toggleSeat(seatId, seatName, seatElement) {
    console.log('Toggling seat:', seatId, seatName); // Debug log

    // Kiểm tra nếu ghế đã được đặt
    if (seatElement.classList.contains('reserved')) {
        alert('Ghế này đã được đặt');
        return;
    }

    // Kiểm tra nếu ghế đang trong trạng thái pending
    if (seatElement.classList.contains('pending')) {
        alert('Ghế này đang được xử lý, vui lòng đợi trong giây lát');
        return;
    }

    if (seatElement.classList.contains('selected')) {
        // Deselect seat
        seatElement.classList.remove('selected');
        seatElement.classList.add('available');
        selectedSeats = selectedSeats.filter(seat => seat.id !== parseInt(seatId));
    } else {
        // Select seat (limit to 8 seats)
        if (selectedSeats.length < maxSeats) {
            seatElement.classList.remove('available');
            seatElement.classList.add('selected');
            selectedSeats.push({
                id: parseInt(seatId),
                name: seatName
            });
            updateSelectedSeatsInfo();
        } else {
            alert('Bạn chỉ có thể chọn tối đa 8 ghế');
        }
    }
}

function updateSelectedSeatsInfo() {
    const selectedSeatsList = document.getElementById('selectedSeatsList');
    selectedSeatsList.innerHTML = selectedSeats.map(seat => 
        `<div class="selected-seat">
            <span>${seat.name}</span>
            <button onclick="toggleSeat(${seat.id}, '${seat.name}', this.parentElement)">
                <i class="fas fa-times"></i>
            </button>
        </div>`
    ).join('');

    // Update total price
    const totalPrice = selectedSeats.length * 75000; // Giá vé 75,000đ
    document.getElementById('totalPrice').textContent = totalPrice.toLocaleString('vi-VN') + 'đ';

    // Enable/disable continue button
    const continueButton = document.getElementById('continueButton');
    continueButton.disabled = selectedSeats.length === 0;
}

function proceedToPayment() {
    if (selectedSeats.length === 0) {
        alert('Vui lòng chọn ít nhất một ghế');
        return;
    }

    // Đánh dấu các ghế đã chọn là pending
    selectedSeats.forEach(seat => {
        const seatElement = document.querySelector(`[data-seat-id="${seat.id}"]`);
        if (seatElement) {
            seatElement.classList.remove('selected');
            seatElement.classList.add('pending');
        }
    });

    // Chuyển hướng đến trang thanh toán với dữ liệu đã chọn
    const seatIds = selectedSeats.map(seat => seat.id).join(',');
    const seatNames = selectedSeats.map(seat => seat.name).join(',');
    const total = selectedSeats.length * 75000;

    window.location.href = `/payment?scheduleId=${this.scheduleId}&seatIds=${seatIds}&seatNames=${seatNames}&total=${total}`;
}

// Export functions
window.seatSelection = {
    initialize: initializeSeatSelection,
    toggleSeat: toggleSeat,
    updateSelectedSeatsInfo: updateSelectedSeatsInfo,
    proceedToPayment: proceedToPayment
}; 