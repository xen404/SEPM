import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Artist} from '../../dtos/artist';
import {ArtistService} from '../../services/artist.service';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-artist',
  templateUrl: './artist.component.html',
  styleUrls: ['./artist.component.css']
})
export class ArtistComponent implements OnInit {

  error: boolean = false;
  errorMessage: string = '';
  artistForm: FormGroup;
  pagination: number = 0;
  page: number = 0;
  pageSize: number = 15;
  paginationLimitUp: number = 15;
  paginationLimitDown: number = 0;
  submitted: boolean = false;
  public searchArtists: Artist[] = [];
  pages: Array<number>;
  totalElements: number;
  search: boolean = false;
  name: string;

  constructor(private artistService: ArtistService, private ngbPaginationConfig: NgbPaginationConfig, private formBuilder: FormBuilder,
              private cd: ChangeDetectorRef, private authService: AuthService) {
    this.artistForm = this.formBuilder.group({
      name: ['', [Validators.required]],
    });
  }

  ngOnInit() {
    this.loadArtistPagination();
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }


  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }


  /**
   * Loads all the artists from the BE
   */
  private loadArtistPagination() {
    this.search = false;
    this.artistService.getArtistPagination(this.page).subscribe(
      data => {
        this.searchArtists = data['content'];
        this.pages = new Array(data['totalPages']);
        this.totalElements = data['totalElements'];
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * searches after an artist with the specified param
   * @param name artists name
   * @param page number of page
   */
  private searchArtist(name: string) {
    this.vanishError();
    this.search = true;
    this.name = name;
    this.artistService.searchArtist(name, this.page).subscribe(data => {
        this.searchArtists = data['content'];
        this.pages = new Array(data['totalPages']);
        this.totalElements = data['totalElements'];
      }, error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  setPage(i, event: any) {
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
    if (this.search === false) {
      this.loadArtistPagination();
    } else {
      this.searchArtist(this.name);
    }
  }


  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  private clearForm() {
    this.artistForm.reset();
    this.submitted = false;
  }
}
