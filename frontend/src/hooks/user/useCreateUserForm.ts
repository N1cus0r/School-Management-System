import * as Yup from "yup";
import { Role } from "../../entities/user/Role.ts";
import { useFormik } from "formik";
import { Gender } from "../../entities/user/Gender.ts";
import useCreateUser from "./useCreateUser.ts";
import { CACHE_KEY_STUDENTS, CACHE_KEY_TEACHERS } from "../../utils/constants.ts";
import { CREATE_USER_VALIDATION_SCHEMA } from "../../schemas/user-schemas.ts";

const useCreateUserForm = (role: Role, onSuccess: () => void) => {
  const createUser = useCreateUser(
    role === Role.TEACHER ? CACHE_KEY_TEACHERS : CACHE_KEY_STUDENTS,
    onSuccess,
  );

  return useFormik({
    initialValues: {
      role,
      gender: Gender.MALE,
      fullName: "",
      email: "",
      password: "",
      dateOfBirth: "",
      mobilePhone: "",
    },
    validationSchema: Yup.object(CREATE_USER_VALIDATION_SCHEMA),
    onSubmit: (values) => {
      createUser.mutate({
        ...values,
        mobilePhone:
          values.mobilePhone.length === 0 ? null : values.mobilePhone,
      });
    },
  });
};

export default useCreateUserForm;
