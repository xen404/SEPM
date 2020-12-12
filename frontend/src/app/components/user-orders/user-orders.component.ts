import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Ticket} from '../../dtos/ticket';
import {ShowService} from '../../services/show.service';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';
import {Show} from '../../dtos/show';
import {formatDate} from '@angular/common';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {EventSimple} from '../../dtos/eventsimple';
import {Artist} from '../../dtos/artist';
import {EventService} from '../../services/event.service';

@Component({
  selector: 'app-my-orders',
  templateUrl: './user-orders.component.html',
  styleUrls: ['./user-orders.component.css'],
})
export class UserOrdersComponent implements OnInit {
  public ticketList: Ticket[];
  public originalUser: User;
  public canceled = false;
  public reserved = false;
  public purchased = false;
  public ticketMatrix: Ticket[][];
  public orderIdArray: number[];
  public form: FormGroup;
  public ordersData = [];

  show: Show;
  event: EventSimple;
  artists: Artist[];
  startDate: string;
  endDate: string;
  startTime: string;
  endTime: string;
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
  cardForm: FormGroup;
  paypalForm: FormGroup;
  immediateForm: FormGroup;
  submitted: boolean = false;
  bonusPointCredit: number = 0;
  reservedTickets: Ticket[];
  ticketIdsToPurchase: number[];
  paying: boolean = false;
  paymentSuccessful: boolean = false;
  paymentError: boolean = false;
  paymentErrorMessage: string = '';
  orderId: number;

  constructor(private route: ActivatedRoute,
              private userService: UserService,
              private showService: ShowService,
              private formBuilder: FormBuilder,
              private eventService: EventService) {
  }

  ngOnInit(): void {
    this.originalUser = this.userService.getLoggedInUser();
    this.getAllTicketsByUserId();
    this.setUpForm();
  }

  private getAllTicketsByUserId() {
    this.showService.getAllTicketsByUserId().subscribe(
      (ticketList: Ticket[]) => {
        this.ticketList = ticketList;
        this.getArrayOfOrderIds(ticketList);
      });
  }

  private getArrayOfOrderIds(ticketList: Ticket[]) {
    this.orderIdArray = [];
    for (let i = 0; i < ticketList.length; i++) {
      this.orderIdArray[i] = ticketList[i].orderId;

    }
    this.orderIdArray = Array.from(new Set(this.orderIdArray));
    this.orderIdArray.sort();
    this.createTicketMatrix(ticketList, this.orderIdArray);
  }

  private createTicketMatrix(ticketList: Ticket[], orderIdArray: number[]) {
    this.ticketMatrix = [];
    for (let i = 0; i < orderIdArray.length; i++) {
      this.ticketMatrix[i] = [];
      for (let j = 0; j < ticketList.length; j++) {
        if (orderIdArray[i] === ticketList[j].orderId) {
          this.ticketMatrix[i].push(ticketList[j]);
        }
      }
    }
  }

  setUpForm() {
    this.cardForm = this.formBuilder.group({
      owner: new FormControl('', Validators.required),
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
      emailAddress: new FormControl(localStorage.getItem('loggedInUserEmail'), [Validators.required, Validators.pattern('([\\w-\\.]+)@((?:[\\w]+\\.)+)([a-zA-Z]{2,4})')]),
      password: new FormControl('', Validators.required)
    });
    this.immediateForm = new FormGroup({
      bank: new FormControl(this.banks[0], Validators.required),
      signatoryNumber: new FormControl('', [Validators.required, Validators.pattern('^[0-9]{8}$')]),
      pin: new FormControl('', [Validators.required, Validators.pattern('^[0-9]{5}$')])
    });
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

  /**
   * Purchase selected reserved tickets
   */
  purchaseTickets() {
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
      this.showService.purchaseReservedTickets(this.ticketIdsToPurchase).subscribe(
        (oid: number) => {
          this.orderId = oid;
          console.log('Purchased previously reserved tickets in new order: ' + this.orderId);
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

  onTicketsToPurchaseChange(e) {
    if (e.target.checked) {
      this.ticketIdsToPurchase.push(Number(e.target.value));
    } else {
      this.ticketIdsToPurchase = this.ticketIdsToPurchase.filter( (item) => item !== Number(e.target.value));
    }
    this.calculateTotalPriceAndBonus();
  }

  calculateTotalPriceAndBonus() {
    this.totalPrice = this.reservedTickets.filter(
      item => this.ticketIdsToPurchase.includes(item.id)).map(item => item.price).reduce(function(a, b) {
      return a + b;
    }, 0);
    this.bonusPointCredit = Math.round(this.totalPrice * 0.1);
  }

  /**
   * Loads the specified show details
   * @param id the id of the show which details should be shown
   */
  loadShowDetails(id: number) {
    this.showService.getShowById(id).subscribe(
      (show: Show) => {
        this.show = show;
        this.startDate = formatDate(this.show.startDate, 'd. MMMM y', 'en-US');
        this.endDate = formatDate(this.show.endDate, 'd. MMMM y', 'en-US');
        this.startTime = this.show.startTime.toString().substring(0, 5);
        this.endTime = this.show.endTime.toString().substring(0, 5);
        this.eventService.getSimpleEventById(show.eventId).subscribe(
          (event: EventSimple) => {
            this.event = event;
            this.artists = event.artists;
          }
        );
      }
    );
  }

  setTicketsToPurchase(tickets: Ticket[]) {
    this.reservedTickets = tickets.sort((a, b) => a.id - b.id);
    this.ticketIdsToPurchase = this.reservedTickets.map(item => item.id);
    this.loadShowDetails(this.reservedTickets[0].showId);
    this.paying = false;
    this.paymentSuccessful = false;
    this.paymentError = false;
    this.calculateTotalPriceAndBonus();
  }

  changePaymentType() {
    this.submitted = false;
    this.setUpForm();
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
/*    cancelTicket(ticketId: number, ticketStatus: string) {
       this.showService.cancelTicket(ticketId).subscribe(
         (bonusPointsCharged) => {
           if (bonusPointsCharged > 0) {
             const bonusPointsInEuro = bonusPointsCharged * 10;
             console.log('The user is charged' + bonusPointsInEuro + '€');
           }

          if (ticketStatus === 'PURCHASED') {
             this.purchased = true;
          }
          if (ticketStatus === 'RESERVED') {
             this.reserved = true;
          }
          this.canceled = true;
          console.log('Cancel ticket with id: ', ticketId);
         }

       );
     }*/

  private getShowById(id: number): Show {
    return null;
  }
}
