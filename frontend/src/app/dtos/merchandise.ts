export class Merchandise {
  constructor(
    public id: number,
    public title: string,
    public description: string,
    public price: number,
    public bonusPoints: number,
    public eventId: number,
    public bonus: boolean,
    public image: string,
    public imagePresent: boolean) {
  }
}
