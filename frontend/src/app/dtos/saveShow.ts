import {Show} from './show';
import {Ticket} from './ticket';

export class SaveShow {
  constructor(
    public show: Show,
    public tickets: Ticket[]) {
  }
}
