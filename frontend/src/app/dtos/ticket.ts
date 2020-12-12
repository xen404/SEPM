import {Seat} from './seat';
import {Show} from './show';
import {Time} from '@angular/common';

export class Ticket {
  constructor(
    public id: number,
    public seatId: number,
    public sector: string,
    public price: number,
    public orderId: number,
    public status: string,
    public seat: Seat,
    public showTitle: number,
    public showStartTime: Time,
    public showStartDate: Date,
    public showId: number) {
  }
}
