const state = {
  movies: [],
  showings: [],
  selectedDate: null,
  selectedShowing: null,
  selectedSeats: new Set(),
  seatResponse: null,
};

const movieGrid = document.querySelector("#movie-grid");
const pageStatus = document.querySelector("#page-status");
const bookingDialog = document.querySelector("#booking-dialog");
const dialogContent = document.querySelector("#dialog-content");
const manageDialog = document.querySelector("#manage-dialog");
const escapeHtml = (value) => String(value).replace(/[&<>"']/g, (character) => ({
  "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;",
})[character]);
const money = (amount) => new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR" }).format(amount);
const displayDate = (date) => new Intl.DateTimeFormat("en-US", { weekday: "short", month: "short", day: "numeric" }).format(date);
const displayTime = (dateTime) => new Intl.DateTimeFormat("en-US", { hour: "numeric", minute: "2-digit" }).format(new Date(dateTime));
const dateValue = (date) => `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;

async function requestJson(url, options) {
  const response = await fetch(url, options);
  const body = await response.json();
  if (!response.ok) throw new Error(body.message || "Something went wrong. Please try again.");
  return body;
}

function buildDates() {
  const options = document.querySelector("#date-options");
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const dates = Array.from({ length: 7 }, (_, index) => {
    const date = new Date(today);
    date.setDate(date.getDate() + index + 1);
    return date;
  });
  state.selectedDate = dateValue(dates[0]);
  options.innerHTML = dates.map((date) => {
    const value = dateValue(date);
    const weekday = new Intl.DateTimeFormat("en-US", { weekday: "short" }).format(date);
    const day = new Intl.DateTimeFormat("en-US", { day: "numeric" }).format(date);
    return `<button class="date-button ${value === state.selectedDate ? "active" : ""}" type="button" data-date="${value}"><span>${weekday}</span><span>${day}</span></button>`;
  }).join("");
}

async function loadCatalogue() {
  pageStatus.textContent = "Finding a good film…";
  try {
    [state.movies] = await Promise.all([
      requestJson("/api/movies"),
      loadShowings(),
    ]);
    renderMovies();
  } catch (error) {
    pageStatus.textContent = error.message;
  }
}

async function loadShowings() {
  state.showings = await requestJson(`/api/showings?date=${encodeURIComponent(state.selectedDate)}`);
}

function renderMovies() {
  if (!state.movies.length) {
    pageStatus.textContent = "No films are on the schedule just yet.";
    movieGrid.innerHTML = "";
    return;
  }
  pageStatus.textContent = "";
  movieGrid.innerHTML = state.movies.map((movie, index) => {
    const showings = state.showings.filter((showing) => showing.movieId === movie.id);
    const times = showings.length
      ? showings.map((showing) => `
        <button class="showtime-button" type="button" data-showing="${showing.id}">
          ${displayTime(showing.startsAt)}
          <small>${money(showing.ticketPrice)} · ${escapeHtml(showing.screenName)}</small>
        </button>`).join("")
      : '<span class="no-showtimes">No showtimes on this day</span>';
    return `
      <article class="movie-card">
        <div class="poster">
          <img src="${escapeHtml(movie.posterUrl)}" alt="${escapeHtml(movie.title)} poster" loading="lazy">
          <span class="poster-index">${String(index + 1).padStart(2, "0")}</span>
          <span class="poster-rating">${escapeHtml(movie.rating)}</span>
        </div>
        <div class="movie-info">
          <div class="movie-heading"><h3>${escapeHtml(movie.title)}</h3><span class="runtime">${movie.runtimeMinutes} MIN</span></div>
          <p class="movie-meta">${escapeHtml(movie.genre.toUpperCase())}</p>
          <p class="synopsis">${escapeHtml(movie.synopsis)}</p>
          <div class="showtime-list" aria-label="Showtimes for ${escapeHtml(movie.title)}">${times}</div>
        </div>
      </article>`;
  }).join("");
}

async function chooseShowing(showingId) {
  state.selectedShowing = state.showings.find((showing) => showing.id === Number(showingId));
  if (!state.selectedShowing) return;
  try {
    state.seatResponse = await requestJson(`/api/showings/${showingId}/seats`);
    state.selectedSeats = new Set();
    renderSeatPicker();
    bookingDialog.showModal();
  } catch (error) {
    pageStatus.textContent = error.message;
  }
}

function renderSeatPicker(error = "") {
  const showing = state.selectedShowing;
  const seats = state.seatResponse.seats;
  const rows = [...new Set(seats.map((seat) => seat.label[0]))];
  const seatRows = rows.map((row) => {
    const rowSeats = seats.filter((seat) => seat.label[0] === row);
    const buttons = rowSeats.map((seat) => {
      const selected = state.selectedSeats.has(seat.label);
      return `<button class="seat ${selected ? "selected" : ""}" type="button" data-seat="${seat.label}" aria-label="Seat ${seat.label}${seat.reserved ? ", reserved" : selected ? ", selected" : ", available"}" aria-pressed="${selected}" ${seat.reserved ? "disabled" : ""}>${seat.label.slice(1)}</button>`;
    }).join("");
    return `<div class="seat-row"><span class="row-label">${row}</span>${buttons}<span class="row-label">${row}</span></div>`;
  }).join("");
  const selectedLabels = [...state.selectedSeats].sort().join(", ") || "Choose your seats";
  const total = showing.ticketPrice * state.selectedSeats.size;
  dialogContent.innerHTML = `
    <p class="dialog-eyebrow">Pick your perfect spot</p>
    <h2 class="dialog-title" id="dialog-title">${escapeHtml(showing.movieTitle)}</h2>
    <p class="dialog-subtitle">${displayDate(new Date(showing.startsAt))} · ${displayTime(showing.startsAt)} · ${escapeHtml(showing.screenName)} · ${money(showing.ticketPrice)} per seat</p>
    <div class="screen-label">SCREEN</div>
    <div class="seat-legend"><span><i class="seat-dot"></i> Available</span><span><i class="seat-dot selected"></i> Selected</span><span><i class="seat-dot reserved"></i> Reserved</span></div>
    <div class="seat-map">${seatRows}</div>
    <div class="seat-summary"><span>${escapeHtml(selectedLabels)}</span><strong>${money(total)}</strong></div>
    <form class="details-form" id="booking-form">
      <label>Your name<input name="customerName" required maxlength="120" autocomplete="name" placeholder="Name for the booking"></label>
      <label>Email address<input name="email" type="email" required maxlength="254" autocomplete="email" placeholder="Your confirmation goes here"></label>
      <p class="form-error" role="alert">${escapeHtml(error)}</p>
      <button class="primary-button" type="submit" ${state.selectedSeats.size === 0 ? "disabled" : ""}>Reserve ${state.selectedSeats.size || "your"} seat${state.selectedSeats.size === 1 ? "" : "s"} <span>${money(total)} →</span></button>
    </form>`;
}

function renderConfirmation(booking) {
  dialogContent.innerHTML = `
    <div class="confirm-mark" aria-hidden="true">✓</div>
    <p class="dialog-eyebrow">It's a date</p>
    <h2 class="dialog-title" id="dialog-title">Your seats are <em>saved.</em></h2>
    <p class="confirmation-copy">${escapeHtml(booking.movieTitle)} · ${displayDate(new Date(booking.startsAt))}, ${displayTime(booking.startsAt)}<br>${escapeHtml(booking.theatre)}, ${escapeHtml(booking.screenName)} · Seats ${escapeHtml(booking.seatLabels.join(", "))}</p>
    <div class="reference-box"><span>YOUR BOOKING REFERENCE</span><strong>${escapeHtml(booking.reference)}</strong></div>
    <p class="confirmation-copy">A confirmation is ready for ${escapeHtml(booking.email)}. Keep your reference handy to look up or cancel your booking.</p>
    <button class="primary-button" type="button" data-close-dialog>Lovely, thank you <span>✳</span></button>`;
}

document.querySelector("#date-options").addEventListener("click", async (event) => {
  const button = event.target.closest("[data-date]");
  if (!button) return;
  state.selectedDate = button.dataset.date;
  document.querySelectorAll(".date-button").forEach((item) => item.classList.toggle("active", item === button));
  pageStatus.textContent = "Checking showtimes…";
  try {
    await loadShowings();
    renderMovies();
  } catch (error) {
    pageStatus.textContent = error.message;
  }
});

movieGrid.addEventListener("click", (event) => {
  const button = event.target.closest("[data-showing]");
  if (button) chooseShowing(button.dataset.showing);
});

dialogContent.addEventListener("click", (event) => {
  if (event.target.closest("[data-close-dialog]")) {
    bookingDialog.close();
    return;
  }
  const button = event.target.closest("[data-seat]");
  if (!button || button.disabled) return;
  const label = button.dataset.seat;
  if (state.selectedSeats.has(label)) state.selectedSeats.delete(label);
  else if (state.selectedSeats.size < 8) state.selectedSeats.add(label);
  renderSeatPicker();
});

dialogContent.addEventListener("submit", async (event) => {
  if (event.target.id !== "booking-form") return;
  event.preventDefault();
  if (state.selectedSeats.size === 0) return;
  const form = event.target;
  const submit = form.querySelector('button[type="submit"]');
  submit.disabled = true;
  try {
    const booking = await requestJson("/api/bookings", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        customerName: form.elements.customerName.value,
        email: form.elements.email.value,
        showingId: state.selectedShowing.id,
        seatLabels: [...state.selectedSeats],
      }),
    });
    renderConfirmation(booking);
    await loadShowings();
    renderMovies();
  } catch (error) {
    renderSeatPicker(error.message);
  }
});

document.querySelectorAll("[data-close-dialog]").forEach((button) => button.addEventListener("click", () => {
  bookingDialog.close();
  manageDialog.close();
}));

document.querySelector("#manage-booking").addEventListener("click", () => {
  document.querySelector("#manage-error").textContent = "";
  document.querySelector("#manage-result").innerHTML = "";
  document.querySelector("#find-booking-form").reset();
  manageDialog.showModal();
});

document.querySelector("#find-booking-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const result = document.querySelector("#manage-result");
  const error = document.querySelector("#manage-error");
  error.textContent = "";
  result.innerHTML = "";
  const reference = form.elements.reference.value.trim().toUpperCase();
  const email = form.elements.email.value.trim();
  try {
    const booking = await requestJson(`/api/bookings/${encodeURIComponent(reference)}?email=${encodeURIComponent(email)}`);
    result.innerHTML = `
      <div class="manage-result">
        <h3>${escapeHtml(booking.movieTitle)}</h3>
        <p>${displayDate(new Date(booking.startsAt))} · ${displayTime(booking.startsAt)}</p>
        <p>${escapeHtml(booking.screenName)} · Seats ${escapeHtml(booking.seatLabels.join(", ") || "—")}</p>
        <p>Reference ${escapeHtml(booking.reference)} · ${escapeHtml(booking.status)} · ${money(booking.total)}</p>
        ${booking.status === "CONFIRMED" ? '<button class="cancel-button" type="button" id="cancel-booking">Cancel this booking</button>' : ""}
      </div>`;
    const cancel = result.querySelector("#cancel-booking");
    if (cancel) cancel.addEventListener("click", async () => {
      cancel.disabled = true;
      try {
        await requestJson(`/api/bookings/${encodeURIComponent(reference)}?email=${encodeURIComponent(email)}`, { method: "DELETE" });
        result.innerHTML = '<div class="manage-result"><h3>Booking cancelled</h3><p>Your seats are available to book again. We hope to see you another time.</p></div>';
        await loadShowings();
        renderMovies();
      } catch (cancelError) {
        error.textContent = cancelError.message;
        cancel.disabled = false;
      }
    });
  } catch (requestError) {
    error.textContent = requestError.message;
  }
});

buildDates();
loadCatalogue();
