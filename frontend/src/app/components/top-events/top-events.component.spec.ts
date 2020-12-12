import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TopEventsComponent } from './top-events.component';

describe('TopEventsComponent', () => {
  let component: TopEventsComponent;
  let fixture: ComponentFixture<TopEventsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TopEventsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TopEventsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
