import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Ticket} from '../../dtos/ticket';
import {ActivatedRoute} from '@angular/router';
import {UserService} from '../../services/user.service';
import {ShowService} from '../../services/show.service';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {formatDate} from '@angular/common';
import {Show} from '../../dtos/show';
import {EventSimple} from '../../dtos/eventsimple';
import {Artist} from '../../dtos/artist';
import {EventService} from '../../services/event.service';


@Component({
  selector: 'app-single-order',
  templateUrl: './single-order.component.html',
  styleUrls: ['./single-order.component.css']
})
export class SingleOrderComponent implements OnInit {
  @Input() ticketList: Ticket[];
  @Input() orderNumber: number;
  @Output() purchaseTickets = new EventEmitter<Ticket[]>();

  public form: FormGroup;
  public canceled = false;
  public reserved = false;
  public purchased = false;
  public successful = false;
  public expandCheckBoxes = false;
  public ticketStatus = 'PURCHASED';
  public ticketsToCancel: number[];
  public bonusPointsInEuro: number = 0;
  public cancelTicketSuccess: string = '';


  show: Show;
  endTime: string;
  past: boolean = false;
  orderType: string;


  constructor(private route: ActivatedRoute,
              private userService: UserService,
              private showService: ShowService,
              private formBuilder: FormBuilder) {
    this.form = this.formBuilder.group({
      checkArray: this.formBuilder.array([], [Validators.required])
    });
  }

  ngOnInit(): void {
    this.orderType = this.ticketList[0].status;
    this.loadShowDetails(this.ticketList[0].showId);
  }

  onCheckboxChange(e) {
    const checkArray: FormArray = this.form.get('checkArray') as FormArray;

    if (e.target.checked) {
      checkArray.push(new FormControl(e.target.value));
    } else {
      let i: number = 0;
      checkArray.controls.forEach((item: FormControl) => {
        if (item.value === e.target.value) {
          checkArray.removeAt(i);
          return;
        }
        i++;
      });
    }
  }

  submit() {

  }

  private changeDate(date: Date): string {
    return formatDate(date, 'd. MMMM y', 'en-US');

  }

  exportCancelTicketPdf() {
    console.log('beee: ', this.ticketsToCancel);

    this.showService.exportCancelTicketPdf(this.ticketList[0].orderId, this.ticketsToCancel, this.bonusPointsInEuro).subscribe(x => {
      const blob = new Blob([x], {type: 'application/pdf'});

      if (window.navigator && window.navigator.msSaveOrOpenBlob) {
        window.navigator.msSaveOrOpenBlob(blob);
        return;
      }
      const data = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = data;
      link.download = 'Canceled_ticket.pdf';
      link.dispatchEvent(new MouseEvent('click', {bubbles: true, cancelable: true, view: window}));

      setTimeout(function () {
        window.URL.revokeObjectURL(data);
        link.remove();
      }, 100);
    });

  }

   exportPurchasedTicketPdf() {
      this.showService.exportPurchasedTicketPdf(this.ticketList[0].orderId).subscribe(x => {

      console.log("ttt" + this.orderNumber+1)
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

  cancelSuccessful(status) {
    this.ticketStatus = status;
    this.successful = true;
    this.cancelTicket();
  }

  cancelTicket() {
    this.ticketsToCancel = this.form.value.checkArray;

    this.showService.cancelTicket(this.ticketsToCancel).subscribe(
      (bonusPointsCharged) => {
        if (bonusPointsCharged > 0) {
        this.bonusPointsInEuro = bonusPointsCharged * 2;
        console.log('The user is charged' + this.bonusPointsInEuro + '€');
        this.cancelTicketSuccess = 'Because you have already redeemed all the bonus points you have received for this order, you are charged: ';
            this.cancelTicketSuccess += this.bonusPointsInEuro + '€.';
        } else {
          this.bonusPointsInEuro = 0;
          this.cancelTicketSuccess = '';
        }
        if (this.ticketStatus === 'PURCHASED') {
          this.purchased = true;
        }
        if (this.ticketStatus === 'RESERVED') {
          this.reserved = true;
        }
        this.canceled = true;
      }
    );
  }



  /**
   * Loads the specified show details
   * @param id the id of the show which details should be shown
   */
  loadShowDetails(id: number) {
    this.showService.getShowById(id).subscribe(
      (show: Show) => {
        this.show = show;
        if (Date.parse(this.show.endDate.toString()) < Date.now()) {
          this.past = true;
        }
      }
    );
  }

  emitTickets(): void {
    this.purchaseTickets.emit(this.ticketList);
  }

  showCheckBoxes() {
    this.expandCheckBoxes = !this.expandCheckBoxes;
  }
}
