import { User } from "../../entities/user/User.ts";
import { useFormik } from "formik";
import * as Yup from "yup";
import { UPDATE_USER_VALIDATION_SCHEMA } from "../../schemas/user-schemas.ts";
import useUpdateProfile from "./useUpdateProfile.ts";

const useUpdateProfileForm = (user: User, onSuccess: () => void) => {
  const updateProfile = useUpdateProfile(["user", user.email], onSuccess);
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
      updateProfile.mutate([
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

export default useUpdateProfileForm;
