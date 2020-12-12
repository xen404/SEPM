import {User} from './user';

export class OrderTickets {
  constructor(
    public seatIds: number[],
    public mode: string) {
  }
}
