import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {User} from '../dtos/user';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userBaseUri: string = this.globals.backendUri + '/users';
  private loggedInUser: User;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getLoggedInUser(): User {
    if (this.loggedInUser != null) {
      return this.loggedInUser;
    } else {
      this.getUserByEmail(localStorage.getItem('loggedInUserEmail')).subscribe(
        (user: User) => {
          this.setLoggedInUser(user);
          return user;
        }
      );
    }
  }

  setLoggedInUser(user: User): void {
    this.loggedInUser = user;
  }

  /**
   * Loads all users from the backend.
   */
  getAllUsers(page: number) {
    console.log('Load all user accounts');
    return this.httpClient.get(this.userBaseUri + '/pages/' + page);
  }

  /**
   * Loads all users from the backend with given access.
   */
  getUsersWithAccess(access: boolean): Observable<User[]> {
    console.log('Load user accounts with access:' + access);
    const params = new HttpParams().set('access', `${access}`);
    return this.httpClient.get<User[]>(this.userBaseUri + '/access', {params} );
  }

  /**
   * Loads specific user from the backend
   * @param email of user to load
   */
  getUserByEmail(email: string): Observable<User> {
    console.log('Load user to ' + email);
    const params = new HttpParams().set('email', email);
    return this.httpClient.get<User>(this.userBaseUri + '/email', {params});
  }

  /**
   * Sign up a new user.
   * @param signUpRequest User data
   */
  signUpCustomer(user: User): Observable<User> {
    console.log('Save user details for ' + user.email);
    return this.httpClient.post<User>(this.userBaseUri + '/signUpCustomer', user);
  }

  /**
   * Sign up a new user.
   * @param signUpRequest User data
   */
  signUpUser(user: User): Observable<User> {
    console.log('Save user details for ' + user.email);
    return this.httpClient.post<User>(this.userBaseUri + '/signUpUser', user);
  }


  /**
   * Searches for user which email address contains given criteria
   * @param criteria the search criteria
   */
  searchUser(criteria: string, page: number): Observable<User[]> {
    console.log('Search  for user with: ' + criteria);
    const params = new HttpParams().set('criteria', criteria);
    return this.httpClient.get<User[]>(this.userBaseUri + '/search/pages/' + page, {params});
  }

  /**
   * Lock an user account.
   * @param email User email
   */
  lockUser(user: User): Observable<User> {
    console.log('Lock user access for user: ' + user.email);
    return this.httpClient.patch<User>(this.userBaseUri + '/lock', user);
  }

  /**
   * Unlock an user account.
   * @param email User email
   */
  unlockUser(user: User): Observable<User> {
    console.log('Unlock user access for user: ' + user.email);
    // const params = new HttpParams().set('email', email);
    return this.httpClient.patch<User>(this.userBaseUri + '/unlock', user);
  }

  /**
   * Updates an user profile.
   * @param user containing updated information
   *        password is null if if has not been changed
   */
  updateProfile(user: User): Observable<User> {
    console.log('Update user data for user: ' + user.email);
    return this.httpClient.patch<User>(this.userBaseUri + '/' + user.id, user);
  }

  /**
   * Deletes an user account.
   * @param id specifies the user to be deleted
   */
  deleteAccount(id: number): Observable<any> {
    console.log('Delete user account with id: ' + id);
    return this.httpClient.delete<any>(this.userBaseUri + '/' + id);
  }

  /**
   * Updates the users bonus points
   * @param id specifies the user account
   * @param bonusPoints specifies the amount of bonus points to be removed from the users account
   */
  redeemBonus(id: number, bonusPoints: number): Observable<User> {
    bonusPoints = bonusPoints * (-1);
    console.log('Update bonus points (' + bonusPoints + ') for user with id: ' + id);
    const bonus = bonusPoints.toString();
    const params = new HttpParams().set('bonus', bonus);
    return this.httpClient.patch<User>(this.userBaseUri + '/' + id + '/bonus_points', params);
  }
}
