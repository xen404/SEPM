export class ShowSeat {
  constructor(
    public seatId: number,
    public sector: string,
    public rowNr: number,
    public seatNr: number,
    public status: string,
    public price: number) {
  }
}
