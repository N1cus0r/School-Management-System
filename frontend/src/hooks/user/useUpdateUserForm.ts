import * as Yup from "yup";
import { User } from "../../entities/user/User.ts";
import { useFormik } from "formik";
import { UPDATE_USER_VALIDATION_SCHEMA } from "../../schemas/user-schemas.ts";
import useUpdateUser from "./useUpdateUser.ts";
import { Role } from "../../entities/user/Role.ts";
import {
  CACHE_KEY_STUDENTS,
  CACHE_KEY_TEACHERS,
} from "../../utils/constants.ts";

const useUpdateUserForm = (user: User, onSuccess: () => void) => {
  const updateUser = useUpdateUser(
    user.role === Role.TEACHER ? CACHE_KEY_TEACHERS : CACHE_KEY_STUDENTS,
    onSuccess,
  );

  return useFormik({
    initialValues: {
      gender: user.gender,
      fullName: user.fullName,
      email: user.email,
      mobilePhone: user.mobilePhone ? user.mobilePhone : "",
      dateOfBirth: user.dateOfBirth ? user.dateOfBirth : "",
    },
    validationSchema: Yup.object(UPDATE_USER_VALIDATION_SCHEMA),
    onSubmit: (values) => {
      updateUser.mutate([
        user.id,
        {
          ...values,
          mobilePhone:
            values.mobilePhone.length === 0 ? null : values.mobilePhone,
        },
      ]);
    },
  });
};

export default useUpdateUserForm;
