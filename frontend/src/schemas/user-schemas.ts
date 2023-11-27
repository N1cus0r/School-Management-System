import * as Yup from "yup";
import {Gender} from "../entities/user/Gender.ts";
export const UPDATE_USER_VALIDATION_SCHEMA =  {
    gender: Yup.string()
        .oneOf(
            [Gender.MALE, Gender.FEMALE],
            "Gender should be either MALE or FEMALE",
        )
        .required("This field is required"),
    fullName: Yup.string().required("This field is required"),
    email: Yup.string()
        .email("Must be a valid email")
        .required("This field is required"),
    dateOfBirth: Yup.date().nullable(),
    mobilePhone: Yup.string().nullable().matches(
        /^\+373\d{8}$/,
        "Phone number must be a valid Moldovan phone number",
    ),
}

export const CREATE_USER_VALIDATION_SCHEMA = {
    ...UPDATE_USER_VALIDATION_SCHEMA,
    password: Yup.string().required("This field is required"),

}