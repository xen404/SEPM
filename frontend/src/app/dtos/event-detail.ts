import {Artist} from './artist';
import {Show} from './show';

export class EventDetail {
  constructor(
    public id: number,
    public title: string,
    public category: string,
    public description: string,
    public duration: number,
    public startDate: Date,
    public endDate: Date,
    public startTime: string,
    public endTime: string,
    public image: String,
    public artists: Artist[],
    public shows: Show[],
    public imagePresent: boolean
  ) {
  }
}
