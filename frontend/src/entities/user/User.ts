import { Role } from "./Role.ts";
import { Gender } from "./Gender.ts";

export interface User {
  id: number;
  email: string;
  fullName: string;
  role: Role;
  gender: Gender;
  mobilePhone: string;
  dateOfBirth: string;
  profileImageId: string;
}
