import * as Yup from "yup";

export const HOMEWORK_VALIDATION_SCHEMA = {
  text: Yup.string().required("This field is required"),
  dueDate: Yup.date().required("This field is required"),
};
