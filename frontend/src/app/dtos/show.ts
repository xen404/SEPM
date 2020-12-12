import {Location} from './location';

export class Show {
  constructor(
    public id: number,
    public title: string,
    public location: Location,
    public description: string,
    public startTime: string,
    public endTime: string,
    public startDate: Date,
    public endDate: Date,
    public eventId: number,
    public duration: number,
    public eventTitle: string) {
  }
}
