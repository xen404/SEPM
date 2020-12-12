export class User {
  constructor(
    public id: number,
    public firstName: string,
    public surname: string,
    public email: string,
    public password: string,
    public admin: boolean,
    public access: boolean,
    public bonusPoints: number
  ) {}
}
