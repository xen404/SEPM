import { Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../dtos/user';

@Injectable({
  providedIn: 'root'
})
export class EditUserService {

  private userBaseUri: string = this.globals.backendUri + '/users';

  constructor(private httpClient: HttpClient, private globals: Globals) { }


  /**
   * DELETE: specific user in the backend
   * @param id of the user to be deleted
   */
  deleteAccount(id: number): Observable<string> {
    return this.httpClient.delete<string>(this.userBaseUri + '/' + id);
  }

  /**
   * PATCH: update specific user in the backend
   * @param id of the user to be updated
   * @param user body of the user to be updated
   * @return updated user
   */
  updateProfile(id: number, user: User): Observable<User> {
    return this.httpClient.patch<User>(this.userBaseUri + '/' + id, user);
  }
}
