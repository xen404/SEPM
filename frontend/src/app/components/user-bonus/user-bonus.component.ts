import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../services/user.service';
import {User} from '../../dtos/user';
import {MerchandiseService} from '../../services/merchandise.service';
import {Merchandise} from '../../dtos/merchandise';

@Component({
  selector: 'app-user-bonus',
  templateUrl: './user-bonus.component.html',
  styleUrls: ['./user-bonus.component.css']
})
export class UserBonusComponent implements OnInit {

  bonusPoints: number;
  user: User;
  availableMerchandise: Merchandise[] = [];

  success: boolean = false;
  successMessage: string;

  page: number = 0;
  pages: number[];
  paginationLimitUp: number = 15;
  paginationLimitDown: number = 0;
  pageSize: number = 15;
  merchandiseNum: number;

  constructor(private router: Router,
              private userService: UserService,
              private merchandiseService: MerchandiseService,
              private route: ActivatedRoute) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }

  ngOnInit(): void {
    this.loadInfo();
    const successString  = this.route.snapshot.paramMap.get('boughtMerchandise');
    if (successString != null) {
      this.success = true;
      this.successMessage = 'You have successfully bought the item: ' + successString;
    }

  }

  loadInfo() {
    this.userService.getUserByEmail(localStorage.getItem('loggedInUserEmail')).subscribe(
      (user: User) => {
        this.bonusPoints = user.bonusPoints;
        this.user = user;
        this.merchandiseService.getAvailableBonus(this.bonusPoints, this.page).subscribe(
          data => {
            this.availableMerchandise = data['content'];
            this.merchandiseNum = data['totalElements'];
            this.pages = new Array(data['totalPages']);
            this.getImages();
          }, error => {
            console.log('Could not get available bonus due to: ' + error.error.error);
          }
        );
      }
    );
  }

  vanishAlert() {
    this.success = false;
    this.router.navigate(['/user-bonus']);
  }

  /**
   * Load images to all displayed merchandise items
   */
  getImages() {
    for (const m of this.availableMerchandise) {
      this.merchandiseService.getImages(m.id).subscribe(
        (res) => {
          this.availableMerchandise.find(x => x.id === m.id).imagePresent = true;
          this.availableMerchandise.find(x => x.id === m.id).image = res;
        }
      );
    }
  }

  private setPage(i, event: any) {
    event.preventDefault();
    this.page = i;
    if (i > this.paginationLimitUp) {
      this.paginationLimitUp += 15;
      this.paginationLimitDown += 15;
    }
    if (i < this.paginationLimitDown) {
      this.paginationLimitUp -= 15;
      this.paginationLimitDown -= 15;
    }
    this.ngOnInit();
  }
}
