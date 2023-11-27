import { User } from "../../entities/user/User.ts";
import useUpdateUserForm from "../../hooks/user/useUpdateUserForm.ts";
import {
  Button,
  FormControl,
  FormLabel,
  Input,
  Select,
  Stack,
  Text,
} from "@chakra-ui/react";
import { FormikProps } from "formik";
import { Gender } from "../../entities/user/Gender.ts";
import ProfileImageDropzone from "./ProfileImageDropzone.tsx";

interface Props {
  user: User;
  onSuccess: () => void;
  form?: FormikProps<{
    gender: Gender;
    fullName: string;
    email: string;
    mobilePhone: string;
    dateOfBirth: string;
  }>;
  guideText?: string;
}
const UpdateUserForm = ({ user, onSuccess, form, guideText }: Props) => {
  const updateUserForm = form ? form : useUpdateUserForm(user, onSuccess);
  /*
   * drag n drop, pass onSuccess prop so it closes after successful update
   * */
  return (
    <Stack spacing={4}>
      <ProfileImageDropzone user={user} onSuccess={onSuccess} />
      <FormControl id="gender">
        <FormLabel>Gender</FormLabel>
        <Select
          name="gender"
          value={updateUserForm.values.gender}
          onChange={updateUserForm.handleChange}
        >
          <option value="MALE">MALE</option>
          <option value="FEMALE">FEMALE</option>
        </Select>
        {updateUserForm.touched.gender && updateUserForm.errors.gender && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {updateUserForm.errors.gender}
          </Text>
        )}
      </FormControl>
      <FormControl id="fullName">
        <FormLabel>Full Name</FormLabel>
        <Input
          type="text"
          name="fullName"
          placeholder="John Doe"
          value={updateUserForm.values.fullName}
          onChange={updateUserForm.handleChange}
        />
        {updateUserForm.touched.fullName && updateUserForm.errors.fullName && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {updateUserForm.errors.fullName}
          </Text>
        )}
      </FormControl>
      <FormControl id="email">
        <FormLabel>Email Address</FormLabel>
        <Input
          type="email"
          name="email"
          placeholder="john.doe@example.com"
          value={updateUserForm.values.email}
          onChange={updateUserForm.handleChange}
        />
        {updateUserForm.touched.email && updateUserForm.errors.email && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {updateUserForm.errors.email}
          </Text>
        )}
      </FormControl>
      <FormControl id="dateOfBirth">
        <FormLabel>Date Of Birth*</FormLabel>
        <Input
          type="date"
          name="dateOfBirth"
          value={updateUserForm.values.dateOfBirth}
          onChange={updateUserForm.handleChange}
        />
        {updateUserForm.touched.dateOfBirth &&
          updateUserForm.errors.dateOfBirth && (
            <Text color="red.500" fontSize="sm" mt={1}>
              {updateUserForm.errors.dateOfBirth}
            </Text>
          )}
      </FormControl>
      <FormControl id="mobilePhone">
        <FormLabel>Mobile Phone*</FormLabel>
        <Input
          type="tel"
          name="mobilePhone"
          placeholder="+37379123456"
          value={updateUserForm.values.mobilePhone}
          onChange={updateUserForm.handleChange}
        />
        {updateUserForm.touched.mobilePhone &&
          updateUserForm.errors.mobilePhone && (
            <Text color="red.500" fontSize="sm" mt={1}>
              {updateUserForm.errors.mobilePhone}
            </Text>
          )}
      </FormControl>
      <Button
        colorScheme={"blue"}
        variant={"solid"}
        isDisabled={!updateUserForm.dirty || !updateUserForm.isValid}
        onClick={updateUserForm.handleSubmit}
      >
        Edit
      </Button>
      {guideText && (
        <Text color={"gray.500"} textAlign={"center"}>
          {guideText}
        </Text>
      )}
    </Stack>
  );
};
export default UpdateUserForm;
