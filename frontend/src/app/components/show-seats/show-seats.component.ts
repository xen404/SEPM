import {Component, Input, OnInit} from '@angular/core';
import {ShowService} from '../../services/show.service';
import {Show} from '../../dtos/show';
import {ShowSeat} from '../../dtos/show-seat';
import {Price} from '../../dtos/price';
import {OrderTickets} from '../../dtos/orderTickets';
import {EventSimple} from '../../dtos/eventsimple';
import {Artist} from '../../dtos/artist';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-show-seats',
  templateUrl: './show-seats.component.html',
  styleUrls: ['./show-seats.component.css']
})
export class ShowSeatsComponent implements OnInit {
  @Input() id: number;
  @Input() show: Show;
  @Input() event: EventSimple;
  @Input() artists: Artist[];
  @Input() startDate: string;
  @Input() startTime: string;
  @Input() endDate: string;
  @Input() endTime: string;
  error: boolean = false;
  errorMsg: string;
  seats: ShowSeat[];
  containerWidth: number;
  rows: number;
  columns: number;
  detailedSeat: ShowSeat;
  prices: Price[];
  selectedSeats: ShowSeat[] = [];
  totalPrice: number = 0;
  paymentType: string = 'credit';
  months = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'];
  years: number[] = [];
  banks: string[] = [
    'Alpenbank Aktiengesellschaft',
    'Bank Austria Creditanstalt AG',
    'Bank Burgenland',
    'BAWAG',
    'DenizBank AG',
    'Hypo Bank Niederösterreich',
    'Raiffeisenbank',
    'Zveza Bank'];
  paying: boolean = false;
  paymentSuccessful: boolean = false;
  paymentError: boolean = false;
  paymentErrorMessage: string = '';
  reserving: boolean = false;
  reservationSuccessful: boolean = false;
  reservationError: boolean = false;
  reservationErrorMessage: string = '';
  orderId: number;
  userId: number;
  cardForm: FormGroup;
  paypalForm: FormGroup;
  immediateForm: FormGroup;
  submitted: boolean = false;
  bonusPointCredit: number = 0;

  constructor(private showService: ShowService, private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    const year = new Date().getFullYear();
    for (let i = year; i < year + 50; i++) {
      this.years.push(i);
    }
    this.loadSeatDetailsOfShow(this.id);
    this.setUpForm();
  }

  setUpForm() {
    this.cardForm = this.formBuilder.group({
      owner: new FormControl('', Validators.compose([
        Validators.required,
      ])),
      cardNumber: new FormControl('', Validators.compose([
        Validators.required,
        Validators.pattern('^[0-9]{12,16}$')
      ])),
      month: new FormControl('01', Validators.required),
      year: new FormControl(new Date().getFullYear(), Validators.required),
      cvv: new FormControl('', Validators.compose([
        Validators.required,
        Validators.pattern('^[0-9]{3}$')
      ]))
    }, {validator: this.validateCreditCard('cardNumber')});
    this.paypalForm = new FormGroup({
      emailAddress: new FormControl(localStorage.getItem('loggedInUserEmail'), Validators.compose(
        [Validators.required, Validators.pattern('([\\w-\\.]+)@((?:[\\w]+\\.)+)([a-zA-Z]{2,4})')])),
      password: new FormControl('', Validators.required)
    });
    this.immediateForm = new FormGroup({
      bank: new FormControl(this.banks[0], Validators.required),
      signatoryNumber: new FormControl('', [Validators.required, Validators.pattern('^[A-Z0-9]{6,8}$')]),
      pin: new FormControl('', [Validators.required, Validators.pattern('^[0-9]{4,6}$')])
    });
  }

  /**
   * Loads the seat details for the specified show
   * @param id the id of the show which seat details should be shown
   */
  loadSeatDetailsOfShow(id: number) {
    this.showService.getSeatsOfShow(id).subscribe(
      (seats: ShowSeat[]) => {
        this.seats = seats;
        this.seats.sort((a, b) => this.compare(a, b));
        this.rows = this.getNumberOfRows(this.seats);
        this.columns = this.seats.length / this.rows;
        this.containerWidth = this.columns * 2.5;
        this.loadPricesOfShow(id);
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Compares two show-seat elements
   */
  private compare(a: ShowSeat, b: ShowSeat) {
    if (a.rowNr > b.rowNr) { return 1; }
    if (b.rowNr > a.rowNr) { return -1; }
    if (a.seatNr > b.seatNr) { return 1; }
    if (b.seatNr > a.seatNr) { return -1; }
    return 0;
  }

  /**
   * Calculates the number of rows of a venue plan (given as list of ShowSeats)
   */
  private getNumberOfRows(seats: ShowSeat[]): number {
    let rowNr = 0;
    seats.forEach(
      (item) => {
        if (item.rowNr > rowNr) { rowNr = item.rowNr; }
      }
    );

    return rowNr;
  }

  /**
   * Loads the prices for the specified show
   * @param id the id of the show which prices should be shown
   */
  loadPricesOfShow(id: number) {
    this.showService.getPricesOfShow(id).subscribe(
      (prices: Price[]) => {
        this.prices = prices;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Sets the detailed seat
   */
  setDetailedSeat(seat: ShowSeat) {
    if (seat.status !== 'NOSEAT') {
      this.detailedSeat = seat;
    }
  }

  /**
   * Removes the detailed seat
   */
  removeDetailedSeat(seat: ShowSeat) {
    this.detailedSeat = undefined;
  }

  /**
   * Picks a seat
   */
  pickSeat(seat: ShowSeat) {
    if (seat.status === 'FREE') {
      seat.status = 'SELECTED';
      this.selectedSeats.push(seat);
      this.selectedSeats.sort((a, b) => this.compare(a, b));
      this.totalPrice += seat.price;
    } else if (seat.status === 'SELECTED') {
      this.unpickSeat(seat);
    }
  }

  /**
   * Unpick a previously selected seat
   */
  unpickSeat(seat: ShowSeat) {
    seat.status = 'FREE';
    this.selectedSeats = this.selectedSeats.filter(item => item.seatId !== seat.seatId);
    this.totalPrice -= seat.price;
  }

  /**
   * Purchase tickets for selected seats
   */
  purchase() {
    this.submitted = true;
    let validInput = false;
    switch (this.paymentType) {
      case 'credit': {
        validInput = this.cardForm.valid;
        break;
      }
      case 'paypal': {
        validInput = this.paypalForm.valid;
        break;
      }
      case 'immediate': {
        validInput = this.immediateForm.valid;
        break;
      }
    }
    if (validInput) {
      this.paying = true;
      const seatIds = [];
      this.selectedSeats.forEach(item => seatIds.push(item.seatId));
      const orderTickets = new OrderTickets(seatIds, 'PURCHASE');
      this.showService.orderTickets(orderTickets, this.id).subscribe(
        (oid) => {
          this.orderId = oid[0];
          this.bonusPointCredit = oid[1];
          console.log('Purchased tickets with order id: ', oid[0]);
          console.log('Bonus point credit for this order: ', oid[1]);
          setTimeout(() => {
              this.paymentSuccessful = true;
              this.paying = false;
            }, 3000
          );
        },
        error => {
          this.paymentError = true;
          this.paymentErrorMessage = error.error;
        }
      );
    }
  }

  exportPurchasedTicketPdf() {
    this.showService.exportPurchasedTicketPdf(this.orderId).subscribe(x => {
      const blob = new Blob([x], {type: 'application/pdf'});

      if (window.navigator && window.navigator.msSaveOrOpenBlob) {
        window.navigator.msSaveOrOpenBlob(blob);
        return;
      }
      const data = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = data;
      link.download = 'ticket.pdf';
      link.dispatchEvent(new MouseEvent('click', {bubbles: true, cancelable: true, view: window}));

      setTimeout(function () {
        window.URL.revokeObjectURL(data);
        link.remove();
      }, 100);
    });
  }

  /**
   * Reserve tickets for selected seats
   */
  reserve() {
    this.reserving = true;
    const seatIds = [];
    this.selectedSeats.forEach(item => seatIds.push(item.seatId));
    const orderTickets = new OrderTickets(seatIds, 'RESERVE');
    this.showService.orderTickets(orderTickets, this.id).subscribe(
      (oid) => {
        this.orderId = oid[0];
        console.log('Reserved tickets with order id: ', oid[0]);
        setTimeout(() => {
            this.reservationSuccessful = true;
            this.reserving = false;
          }, 2000
        );
      },
      error => {
        this.reservationError = true;
        this.reservationErrorMessage = error.error;
      }
    );
  }

  /**
   * Calculate the bonus points that the user is credited with when buying the ticket(s).
   * 10% of the total price commercially rounded.
   */
  calculateBonusPointCredit() {
    this.bonusPointCredit = Math.round(this.totalPrice * 0.1);
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMsg = error.error.error;
    } else {
      this.errorMsg = error.error;
    }
  }

  changePaymentType() {
    this.submitted = false;
    this.setUpForm();
  }

  /**
   * validates a given card number based on the Luhn Algorithm.
   * @param controlName specifies the control that contains the card number.
   */
  validateCreditCard(controlName) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      if (!control.value) {
        control.setErrors({required: true});
      } else if (/[^0-9-\s]+/.test(control.value)) {
        control.setErrors({pattern: true});
      } else {
        let nCheck = 0, nDigit = 0, bEven = false;
        const value = control.value.replace(/\D/g, '');
        for (let n = value.length - 1; n >= 0; n--) {
          const cDigit = value.charAt(n);
          nDigit = parseInt(cDigit, 10);
          if (bEven) {
            if ((nDigit *= 2) > 9) {
              nDigit -= 9;
            }
          }
          nCheck += nDigit;
          bEven = !bEven;
        }

        if ((nCheck % 10) !== 0) {
          control.setErrors({invalid: true});
        }
      }
    };
  }

}
