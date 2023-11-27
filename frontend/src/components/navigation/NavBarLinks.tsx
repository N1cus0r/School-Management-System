import { Link } from "react-router-dom";
import { Text } from "@chakra-ui/react";

interface Props {
  links: { label: string; href: string }[];
}

const NavBarLinks = ({ links }: Props) => {
  return (
    <>
      {links.map((link) => (
        <Link key={link.label} to={link.href}>
          <Text
            fontWeight={
              location.pathname.includes(link.href) ? "bold" : "normal"
            }
            _hover={{
              textDecoration: "underline",
            }}
          >
            {link.label}
          </Text>
        </Link>
      ))}
    </>
  );
};
export default NavBarLinks;
