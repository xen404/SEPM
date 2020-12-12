import {Artist} from './artist';

export class EventSimple {
  constructor(
    public id: number,
    public title: string,
    public category: string,
    public duration: number,
    public startDate: Date,
    public endDate: Date,
    public startTime: string,
    public endTime: string,
    public artists: Artist[]) {
  }
}


